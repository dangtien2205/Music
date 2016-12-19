package dangtien.tapbi.com.music.mode;

import java.io.Serializable;

/**
 * Created by toannt on 25/11/2016.
 */

public class AlbumInfo{
    private String playlist_id;
    private String title;
    private String artist;
    private String cover;

    public AlbumInfo(String playlist_id, String title, String artist, String cover) {
        this.playlist_id = playlist_id;
        this.title = title;
        this.artist = artist;
        this.cover = cover;
    }

    public String getPlaylist_id() {
        return playlist_id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCover() {
        return cover;
    }
}
