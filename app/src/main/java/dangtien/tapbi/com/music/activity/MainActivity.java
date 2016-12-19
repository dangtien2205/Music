package dangtien.tapbi.com.music.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.fragment.AlbumFragment;
import dangtien.tapbi.com.music.fragment.SongFragment;
import dangtien.tapbi.com.music.manager.MusicPlayer;
import dangtien.tapbi.com.music.mode.SongInfo;

public class MainActivity extends AppCompatActivity {
    public static String TYPE_SONG="song";
    private AlbumFragment albumFragment;
    private SongFragment songFragment;
    private boolean isFisrt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        initControls();
    }

    private void initControls() {
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
            isFisrt=false;
        }
        replaceFragment(albumFragment);
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

    public void playMusic(ArrayList<SongInfo> songInfos,int postion){
        LinearLayout layoutPlay = (LinearLayout)findViewById(R.id.layoutPlay);
        SongInfo songInfo = songInfos.get(postion);
        ((TextView)findViewById(R.id.txtTitlePlay)).setText(songInfo.getTitle());
        ((TextView)findViewById(R.id.txtArtistPlay)).setText(songInfo.getArtist());
        layoutPlay.setVisibility(View.VISIBLE);
        layoutPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MusicPlayerActivity.class);
                intent.setAction("OLD_MUSIC");
                startActivity(intent);
            }
        });
        Intent intent = new Intent(MainActivity.this,MusicPlayerActivity.class);
        intent.setAction("NEW_MUSIC");
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        musicPlayer.stop();
        musicPlayer.setPlaySong(songInfos,postion);
        musicPlayer.setup();
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
}
