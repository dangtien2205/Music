package dangtien.tapbi.com.music.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dangtien.tapbi.com.music.App;
import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.manager.MusicPlayer;
import dangtien.tapbi.com.music.mode.SongInfo;
import dangtien.tapbi.com.music.service.ServiceMedia;
import de.hdodenhof.circleimageview.CircleImageView;

import static dangtien.tapbi.com.music.service.ServiceMedia.CHANGE_ACTION;
import static dangtien.tapbi.com.music.service.ServiceMedia.PLAY_ACTION;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private CircleImageView imageSong;
    private TextView txtNameSong;
    private TextView txtNameSinger;
    private TextView txtTimeTotal;
    private MusicPlayer musicPlayer;
    private ImageView imagePlay;
    private ImageView imageLoop;
    private ImageView imageDisorder;
    private TextView txtBegin;
    private SeekBar seekBar;
    private Handler handler;
    private SongInfo songInfo;
    private ServiceMedia mService;
    private BroadCastMusicPlayer broadCast;
    private Animation animation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_music);

        initializeComponent();
        initializeListener();
        initializeData();

        handlerService();
    }

    private void initializeComponent() {
        imageSong = (CircleImageView) findViewById(R.id.civ_image_cover);
        txtNameSinger = (TextView) findViewById(R.id.txt_player_singer);
        txtNameSong = (TextView) findViewById(R.id.txt_player_name);
        txtTimeTotal = (TextView) findViewById(R.id.txt_total_time);
        imagePlay = (ImageView) findViewById(R.id.btn_play);
        imageLoop = (ImageView) findViewById(R.id.btn_loop);
        imageDisorder = (ImageView) findViewById(R.id.btn_disorder);
        txtBegin = (TextView) findViewById(R.id.txt_begin_time);
        seekBar = (SeekBar) findViewById(R.id.seebar);
    }

    public void initializeListener() {
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_previous).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.txt_128).setOnClickListener(this);
        findViewById(R.id.txt_320).setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        imageDisorder.setOnClickListener(this);
        imagePlay.setOnClickListener(this);
        imageLoop.setOnClickListener(this);
    }

    private void initializeData() {
        animation = AnimationUtils.loadAnimation(MusicPlayerActivity.this, R.anim.rotate);
        musicPlayer = MusicPlayer.getInstance();
        Intent intent = getIntent();
        songInfo = musicPlayer.getSong();
        updateUI(songInfo);
        if (intent.getAction().equals("NEW_MUSIC")) {
            musicPlayer.play();
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_play:
                if (mService != null) {
                    mService.handlerPlayAndPause();
                }
                break;
            case R.id.btn_loop:
                musicPlayer.setLoop();
                if (musicPlayer.isLoop())
                    imageLoop.setImageResource(R.drawable.ic_player_v4_repeat_one);
                else
                    imageLoop.setImageResource(R.drawable.ic_player_v4_repeat_off);
                break;
            case R.id.btn_next:
                if (mService != null) {
                    mService.handlerNext();
                }else {
                    handlerService();
                }
                break;
            case R.id.btn_previous:
                if (mService != null) {
                    mService.handlerPrev();
                }else {
                    handlerService();
                }
                break;
            case R.id.btn_disorder:
                musicPlayer.setDisorder();
                if (musicPlayer.isDisorder())
                    imageDisorder.setImageResource(R.drawable.ic_player_v4_shuffle_on);
                else
                    imageDisorder.setImageResource(R.drawable.ic_player_v4_shuffle_off);
                break;
            case R.id.txt_128:
                downloadSong(songInfo.getSource().get_128(), songInfo.getTitle());
                break;
            case R.id.txt_320:
                downloadSong(songInfo.getSource().get_320(), songInfo.getTitle());
                break;
            default:
                break;
        }
    }

    public void updateUI(SongInfo songInfo) {
        String linkApi = "http://image.mp3.zdn.vn/";
        Glide.with(this).load(linkApi + songInfo.getThumbnail()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageSong.setImageBitmap(resource);
                imageSong.startAnimation(animation);
            }
        });
        txtNameSinger.setText(songInfo.getArtist());
        txtNameSong.setText(songInfo.getTitle());
        int minute = songInfo.getDuration() / 60;
        int second = songInfo.getDuration() % 60;
        String time = "";
        if (minute < 10) time += "0" + minute + ":";
        else time += minute + ":";
        if (second < 10) time += "0" + second;
        else time += second;
        txtTimeTotal.setText(time);
        if (musicPlayer.getState()==MusicPlayer.PLAYER_PLAY) {
            imagePlay.setImageResource(R.drawable.ic_player_v4_pause);
            imageSong.startAnimation(animation);
        } else {
            imagePlay.setImageResource(R.drawable.ic_player_v4_play);
            imageSong.clearAnimation();
        }

        updateTime();
    }

    public void updateTime() {
        handler = new Handler();
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (MusicPlayer.getInstance().getState() == MusicPlayer.PLAYER_IDLE) {
                return;
            }
            long totalDuration = MusicPlayer.getInstance().getMediaPlayer().getDuration();
            long currentDuration = MusicPlayer.getInstance().getMediaPlayer().getCurrentPosition();
            txtBegin.setText(milliSecondsToTimer(currentDuration) + "");
            int progress = (getProgressPercentage(currentDuration, totalDuration));
            seekBar.setProgress(progress);
            handler.postDelayed(this, 100);
        }
    };

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;
        String minutesString;

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        return finalTimerString;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = MusicPlayer.getInstance().getMediaPlayer().getDuration();
        int currentDuration = progressToTimer(seekBar.getProgress(), totalDuration);
        MusicPlayer.getInstance().getMediaPlayer().seekTo(currentDuration);
        updateTime();
    }

    private void downloadSong(String path, String name) {
        DownLoadMusicTask downLoadMusicTask = new DownLoadMusicTask(name);
        downLoadMusicTask.execute(path);
    }

    private class DownLoadMusicTask extends AsyncTask<String, Void, Void> {
        private HttpURLConnection connection;
        private String name;

        public DownLoadMusicTask(String name) {
            this.name = name;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                File sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOWNLOADS);
                File file = new File(sdcard, name + ".mp3");
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream is = connection.getInputStream();
                int count = 0;
                byte[] data = new byte[1024];
                while ((count = is.read(data)) != -1) {
                    fileOutput.write(data, 0, count);
                }
                is.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(App.getContext(), "Tải Thành Công", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUI();
        broadCast = new BroadCastMusicPlayer();
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

    private class BroadCastMusicPlayer extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case CHANGE_ACTION:
                    updateUI(MusicPlayer.getInstance().getSong());
                    break;
                case PLAY_ACTION:
                    if (musicPlayer.getState()==MusicPlayer.PLAYER_PLAY) {
                        imagePlay.setImageResource(R.drawable.ic_player_v4_pause);
                        imageSong.startAnimation(animation);
                    }
                    else {
                        imagePlay.setImageResource(R.drawable.ic_player_v4_play);
                        imageSong.clearAnimation();
                    }
                default:
                    break;
            }
        }
    }

    private void checkUI() {
        if (musicPlayer.getState()==MusicPlayer.PLAYER_PLAY) {
            imagePlay.setImageResource(R.drawable.ic_player_v4_pause);
            imageSong.startAnimation(animation);
        } else {
            imagePlay.setImageResource(R.drawable.ic_player_v4_play);
            imageSong.clearAnimation();
        }
        if (musicPlayer.isLoop())
            imageLoop.setImageResource(R.drawable.ic_player_v4_repeat_one);
        else
            imageLoop.setImageResource(R.drawable.ic_player_v4_repeat_off);
        if (musicPlayer.isDisorder())
            imageDisorder.setImageResource(R.drawable.ic_player_v4_shuffle_on);
        else
            imageDisorder.setImageResource(R.drawable.ic_player_v4_shuffle_off);
    }
}
