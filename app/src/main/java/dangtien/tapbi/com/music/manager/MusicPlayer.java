package dangtien.tapbi.com.music.manager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import dangtien.tapbi.com.music.App;
import dangtien.tapbi.com.music.mode.SongInfo;

public class MusicPlayer {
    public static final int PLAYER_IDLE = -1;
    public static final int PLAYER_PLAY = 1;
    public static final int PLAYER_PAUSE = 2;
    public static final int PLAYER_STOP = 3;
    public static MusicPlayer instance;

    private MediaPlayer mediaPlayer;
    private int state;
    private boolean isLoop;
    private boolean isDisorder;
    private ArrayList<SongInfo> songInfos;
    private int position;

    public static final MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    private MusicPlayer() {
        state = PLAYER_IDLE;
        songInfos = new ArrayList<>();
        isLoop = false;
        isDisorder = false;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setPlaySong(ArrayList<SongInfo> songInfos, int pos) {
        this.songInfos = songInfos;
        position = pos;
    }

    public void setup() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(isLoop);
            mediaPlayer.setDataSource(App.getContext(), Uri.parse(getSong().getSource().get_128()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        state = PLAYER_PLAY;
    }

    public void pause() {
        mediaPlayer.pause();
        state = PLAYER_PAUSE;
    }

    public void resume() {
        mediaPlayer.start();
        state = PLAYER_PLAY;
    }

    public void stop() {
        if (mediaPlayer == null)
            return;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        state = PLAYER_IDLE;
    }

    public void setLoop() {
        mediaPlayer.setLooping(!isLoop);
        isLoop = !isLoop;
    }

    public void setDisorder() {
        mediaPlayer.setLooping(!isDisorder);
        isDisorder = !isDisorder;
    }


    public void nextSong() {
        stop();
        if (isDisorder) {
            int a = position;
            do {
                position = new Random().nextInt(songInfos.size());
            } while (position == a);
        } else {
            if (position == songInfos.size() - 1) {
                position = 0;
            } else {
                position++;
            }
        }
        setup();
        play();
    }

    public void previousSong() {
        stop();
        if (isDisorder) {
            int a = position;
            do {
                position = new Random().nextInt(songInfos.size());
            } while (position == a);
        } else {
            if (position == 0) {
                position = songInfos.size() - 1;
            } else {
                position--;
            }
        }
        setup();
        play();
    }

    public SongInfo getSong() {
        return songInfos.get(position);
    }

    public boolean isLoop() {
        return isLoop;
    }

    public boolean isDisorder() {
        return isDisorder;
    }

    public int getState() {
        return state;
    }

}