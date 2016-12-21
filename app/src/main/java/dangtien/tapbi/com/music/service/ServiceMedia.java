package dangtien.tapbi.com.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.activity.MusicPlayerActivity;
import dangtien.tapbi.com.music.manager.MusicPlayer;
import dangtien.tapbi.com.music.mode.SongInfo;

/**
 * Created by toannt on 20/12/2016.
 */

public class ServiceMedia extends Service {
    private static final int ID_NO_MEDIA=30;
    public static final String CHANGE_ACTION = "CHANGE_ACTION";
    public static final String PLAY_ACTION = "PLAY_ACTION";
    private static final String PREV_ACTION_NO = "PREV_ACTION";
    private static final String PLAY_ACTION_NO = "PLAY_ACTION";
    private static final String NEXT_ACTION_NO = "NEXT_ACTION";
    public static final String STOP_FOREGROUND_ACTION = "STOP_FOREGROUND_ACTION";
    private NotificationManagerCompat notification;
    private NotificationCompat.Builder builder;
    private Notification mNotification;
    private RemoteViews views;
    private RemoteViews smallViews;
    private MusicPlayer musicPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
        notification = NotificationManagerCompat.from(this);
        musicPlayer = MusicPlayer.getInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinderMedia();
    }
    public class MyBinderMedia extends Binder {
        public ServiceMedia getServiceMedia() {
            return ServiceMedia.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (PLAY_ACTION_NO.equals(intent.getAction())) {
            handlerPlayAndPause();
        } else if (STOP_FOREGROUND_ACTION.equals(intent.getAction())) {
            Intent intent1=new Intent();
            intent1.setAction(PLAY_ACTION);
            sendBroadcast(intent1);
            stopForeground(false);
            stopService(intent);
            musicPlayer.pause();
            notification.cancel(ID_NO_MEDIA);
        } else if (NEXT_ACTION_NO.equals(intent.getAction())) {
            handlerNext();
        } else if (PREV_ACTION_NO.equals(intent.getAction())) {
            handlerPrev();
        }
        return START_NOT_STICKY;
    }

    public void handlerNext(){
        musicPlayer.nextSong();
        changeDisplayNo(musicPlayer.getSong());
        startForeground(ID_NO_MEDIA, mNotification);

        Intent intent=new Intent();
        intent.setAction(CHANGE_ACTION);
        sendBroadcast(intent);
    }

    public void handlerPrev() {
        musicPlayer.previousSong();
        changeDisplayNo(musicPlayer.getSong());
        startForeground(ID_NO_MEDIA, mNotification);

        Intent intent=new Intent();
        intent.setAction(CHANGE_ACTION);
        sendBroadcast(intent);
    }

    public void handlerPlayAndPause(){
        switch (musicPlayer.getState()) {
            case MusicPlayer.PLAYER_PAUSE:
                musicPlayer.resume();
                views.setImageViewResource(R.id.civ_pause_no, R.drawable.pause);
                smallViews.setImageViewResource(R.id.civ_pause_no_small, R.drawable.pause);
                break;
            case MusicPlayer.PLAYER_PLAY:
                musicPlayer.pause();
                views.setImageViewResource(R.id.civ_pause_no, R.drawable.play);
                smallViews.setImageViewResource(R.id.civ_pause_no_small, R.drawable.play);
                break;
            default:
                break;
        }
        startForeground(ID_NO_MEDIA, mNotification);

        Intent intent=new Intent();
        intent.setAction(PLAY_ACTION);
        sendBroadcast(intent);
    }

    public void playAudioClickItem(ArrayList<SongInfo> songInfos,int postion) {
        cumstomNotification(songInfos.get(postion));
        musicPlayer.stop();
        musicPlayer.setPlaySong(songInfos,postion);
    }

    private void changeDisplayNo(SongInfo song){
        views.setTextViewText(R.id.txt_name_no,song.getTitle());
        views.setTextViewText(R.id.txt_artist_no,song.getArtist());
        smallViews.setTextViewText(R.id.txt_name_no_small,song.getTitle());
        smallViews.setTextViewText(R.id.txt_artist_no_small,song.getArtist());
        views.setImageViewResource(R.id.civ_pause_no, R.drawable.pause);
        smallViews.setImageViewResource(R.id.civ_pause_no_small, R.drawable.pause);
        String linkApi = "http://image.mp3.zdn.vn/";
        Glide.with(this).load(linkApi + song.getThumbnail()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                views.setImageViewBitmap(R.id.img_avatar_no,resource);
                smallViews.setImageViewBitmap(R.id.img_avatar_notification,resource);
                startForeground(ID_NO_MEDIA, mNotification);
            }
        });
    }
    private void cumstomNotification(SongInfo song){
        views = new RemoteViews(getPackageName(),R.layout.custom_notification);
        smallViews = new RemoteViews(getPackageName(),R.layout.custom_notification_small);

        Intent intentNotification = new Intent();
        intentNotification.setAction("OLD_MUSIC_1");
        intentNotification.setClass(this,MusicPlayerActivity.class);
        intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentNotification,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPrev = new Intent();
        intentPrev.setClass(this, ServiceMedia.class);
        intentPrev.setAction(PREV_ACTION_NO);
        PendingIntent pendingPrev = PendingIntent.getService(this, 0, intentPrev, 0);

        Intent intentPlay = new Intent();
        intentPlay.setClass(this, ServiceMedia.class);
        intentPlay.setAction(PLAY_ACTION_NO);
        PendingIntent pendingPlay = PendingIntent.getService(this, 0, intentPlay, 0);

        Intent intentNext = new Intent();
        intentNext.setClass(this, ServiceMedia.class);
        intentNext.setAction(NEXT_ACTION_NO);
        PendingIntent pendingNext = PendingIntent.getService(this, 0, intentNext, 0);

        Intent intentClose = new Intent();
        intentClose.setClass(this, ServiceMedia.class);
        intentClose.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingClose = PendingIntent.getService(this, 0, intentClose, 0);

        changeDisplayNo(song);
        views.setOnClickPendingIntent(R.id.civ_back_no, pendingPrev);
        views.setOnClickPendingIntent(R.id.civ_pause_no, pendingPlay);
        views.setOnClickPendingIntent(R.id.civ_next_no, pendingNext);
        views.setOnClickPendingIntent(R.id.img_close, pendingClose);

        smallViews.setOnClickPendingIntent(R.id.civ_pause_no_small, pendingPlay);
        smallViews.setOnClickPendingIntent(R.id.civ_next_no_small, pendingNext);
        smallViews.setOnClickPendingIntent(R.id.img_close_small, pendingClose);

        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_player);
        builder.setOngoing(true);
        builder.setAutoCancel(true);
        builder.setContent(smallViews);
        builder.setCustomBigContentView(views);
        builder.setContentIntent(pendingIntent);
        mNotification = builder.build();
        startForeground(ID_NO_MEDIA, mNotification);

    }
}
