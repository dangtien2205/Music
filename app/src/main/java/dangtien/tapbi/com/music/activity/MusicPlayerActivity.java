package dangtien.tapbi.com.music.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import de.hdodenhof.circleimageview.CircleImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_music);

        initializeComponent();
        initializeListener();
        initializeData();
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
        musicPlayer = MusicPlayer.getInstance();
        Intent intent = getIntent();
        songInfo = musicPlayer.getSong();
        updateUI(songInfo);
        Animation animation = AnimationUtils.loadAnimation(MusicPlayerActivity.this, R.anim.rotate);
        imageSong.startAnimation(animation);

        if (intent.getAction().equals("NEW_MUSIC")) {
            musicPlayer.play();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_play:
                switch (musicPlayer.getState()) {
                    case MusicPlayer.PLAYER_PAUSE:
                        musicPlayer.resume();
                        imagePlay.setImageResource(R.drawable.ic_player_v4_pause);
                        break;
                    case MusicPlayer.PLAYER_PLAY:
                        musicPlayer.pause();
                        imagePlay.setImageResource(R.drawable.ic_player_v4_play);
                        break;
                    default:
                        break;
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
                musicPlayer.nextSong();
                updateUI(musicPlayer.getSong());
                break;
            case R.id.btn_previous:
                musicPlayer.previousSong();
                updateUI(musicPlayer.getSong());
                break;
            case R.id.btn_disorder:
                musicPlayer.setDisorder();
                if (musicPlayer.isDisorder())
                    imageDisorder.setImageResource(R.drawable.ic_player_v4_shuffle_on);
                else
                    imageDisorder.setImageResource(R.drawable.ic_player_v4_shuffle_off);
                break;
            case R.id.txt_128:
                    downloadSong(songInfo.getSource().get_128(),songInfo.getTitle());
                break;
            case R.id.txt_320:
                    downloadSong(songInfo.getSource().get_320(),songInfo.getTitle());
                break;
            default:
                break;
        }
    }

    public void updateUI(SongInfo songInfo) {
//        String linkApi="http://image.mp3.zdn.vn/";
//        Glide.with(this).load(linkApi+songInfo.getThumbnail())
//                .into(imageSong);
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
            if (currentDuration == totalDuration) {
                musicPlayer.nextSong();
                updateUI(musicPlayer.getSong());
            }
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
    private class DownLoadMusicTask extends AsyncTask<String,Void, Void> {
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
                    fileOutput.write(data, 0,count);
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
            Toast.makeText(App.getContext(),"Tải Thành Công",Toast.LENGTH_SHORT).show();
        }
    }
}
