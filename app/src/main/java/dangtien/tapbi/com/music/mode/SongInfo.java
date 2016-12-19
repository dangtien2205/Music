package dangtien.tapbi.com.music.mode;

import java.io.Serializable;

/**
 * Created by toannt on 25/11/2016.
 */

public class SongInfo implements Serializable {
    private double song_id;
    private String title;
    private String artist;
    private SourceInfo source;
    private int duration;
    private String link;
    private String thumbnail;

    public SongInfo(double song_id, String title, String artist, SourceInfo source, int duration, String link, String thumbnail) {
        this.song_id = song_id;
        this.title = title;
        this.artist = artist;
        this.source = source;
        this.duration = duration;
        this.link = link;
        this.thumbnail = thumbnail;
    }

    public double getSong_id() {
        return song_id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public SourceInfo getSource() {
        return source;
    }

    public int getDuration() {
        return duration;
    }

    public String getLink() {
        return link;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
