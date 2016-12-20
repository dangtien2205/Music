package dangtien.tapbi.com.music.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.fragment.AlbumFragment;
import dangtien.tapbi.com.music.manager.MusicPlayer;
import dangtien.tapbi.com.music.mode.SongInfo;
import dangtien.tapbi.com.music.service.ServiceMedia;
import de.hdodenhof.circleimageview.CircleImageView;

import static dangtien.tapbi.com.music.R.id.txtArtistPlay;
import static dangtien.tapbi.com.music.service.ServiceMedia.CHANGE_ACTION;
import static dangtien.tapbi.com.music.service.ServiceMedia.PLAY_ACTION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private AlbumFragment albumFragment;
    private ServiceMedia mService;
    private boolean isFisrt;
    private TextView txtTitle;
    private TextView txtArtist;
    private CircleImageView civPre;
    private CircleImageView civPause;
    private CircleImageView civNext;
    private BroadcastReceiver broadCast;
    private LinearLayout layoutPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        initControls();
        initEvents();
        handlerService();
    }

    private void initControls() {
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
            isFisrt=false;
        }
        replaceFragment(albumFragment);
        txtTitle=(TextView)findViewById(R.id.txtTitlePlay);
        txtArtist=(TextView)findViewById(R.id.txtArtistPlay);
        civPre =(CircleImageView)findViewById(R.id.civBack);
        civPause =(CircleImageView)findViewById(R.id.civPlay);
        civNext =(CircleImageView)findViewById(R.id.civNext);
    }

    private void initEvents() {
        civPre.setOnClickListener(this);
        civPause.setOnClickListener(this);
        civNext.setOnClickListener(this);
    }

    public void replaceFragment(Fragment fragment) {
        Fragment f= getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
        if (f!=null) {
            if (fragment.isVisible()){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }
        if (isFisrt==false) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_content, fragment, fragment.getClass().getName())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_content, fragment, fragment.getClass().getName())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        isFisrt=true;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceMedia.MyBinderMedia media = (ServiceMedia.MyBinderMedia) iBinder;
            mService = media.getServiceMedia();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void handlerService() {
        Intent intent = new Intent();
        intent.setClass(this, ServiceMedia.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public void playMusic(ArrayList<SongInfo> songInfos,int postion){
        layoutPlay = (LinearLayout)findViewById(R.id.layoutPlay);
        SongInfo songInfo = songInfos.get(postion);
        ((TextView)findViewById(R.id.txtTitlePlay)).setText(songInfo.getTitle());
        ((TextView)findViewById(txtArtistPlay)).setText(songInfo.getArtist());
        layoutPlay.setVisibility(View.VISIBLE);
        layoutPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MusicPlayerActivity.class);
                intent.setAction("OLD_MUSIC_0");
                startActivity(intent);
            }
        });
        Intent intent = new Intent(MainActivity.this,MusicPlayerActivity.class);
        intent.setAction("NEW_MUSIC");
        if (mService != null) {
            mService.playAudioClickItem(songInfos,postion);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUI();
        broadCast = new BroadCastMainActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CHANGE_ACTION);
        intentFilter.addAction(PLAY_ACTION);
        registerReceiver(broadCast, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadCast);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.civNext:
                if (mService != null) {
                    mService.handlerNext();
                }
                break;
            case R.id.civPlay:
                if (mService != null) {
                    mService.handlerPlayAndPause();
                }
                break;
            case R.id.civBack:
                if (mService != null) {
                    mService.handlerPrev();
                }
                break;
            default:
                break;
        }
    }

    private class BroadCastMainActivity extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case CHANGE_ACTION:
                    updateUI(MusicPlayer.getInstance().getSong());
                    break;
                case PLAY_ACTION:
                    if (MusicPlayer.getInstance().getMediaPlayer().isPlaying())
                        civPause.setImageResource(R.drawable.ic_av_pause_over_video_large);
                    else
                        civPause.setImageResource(R.drawable.ic_av_play_over_video_large);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateUI(SongInfo song) {
        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());
    }

    private void checkUI(){
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if(musicPlayer.getMediaPlayer()==null){
            return;
        }
        layoutPlay.setVisibility(View.VISIBLE);
        if (MusicPlayer.getInstance().getMediaPlayer().isPlaying())
            civPause.setImageResource(R.drawable.ic_av_pause_over_video_large);
        else
            civPause.setImageResource(R.drawable.ic_av_play_over_video_large);
        updateUI(MusicPlayer.getInstance().getSong());
    }
}
