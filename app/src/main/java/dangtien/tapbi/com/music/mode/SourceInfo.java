package dangtien.tapbi.com.music.mode;

import com.google.gson.annotations.SerializedName;

/**
 * Created by toannt on 25/11/2016.
 */

public class SourceInfo {
    @SerializedName("128")
    private String _128;
    @SerializedName("320")
    private String _320;
    @SerializedName("lossless")
    private String lossless;

    public SourceInfo(String _128, String lossless, String _320) {
        this._128 = _128;
        this.lossless = lossless;
        this._320 = _320;
    }

    public String get_128() {
        return _128;
    }

    public String get_320() {
        return _320;
    }

    public String getLossless() {
        return lossless;
    }
}
