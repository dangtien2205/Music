package dangtien.tapbi.com.music.mode;

/**
 * Created by toannt on 25/11/2016.
 */

public class SongInfo {
    private double song_id;
    private String title;
    private String artist;
    private SourceInfo source;
    private String link;
    private String thumbnail;

    public SongInfo(double song_id, String title, String artist, SourceInfo source, String link, String thumbnail) {
        this.song_id = song_id;
        this.title = title;
        this.artist = artist;
        this.source = source;
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

    public String getLink() {
        return link;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
