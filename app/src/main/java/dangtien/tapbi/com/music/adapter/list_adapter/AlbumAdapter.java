package dangtien.tapbi.com.music.adapter.list_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.mode.AlbumInfo;

/**
 * Created by toannt on 25/11/2016.
 */

public class AlbumAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AlbumInfo> albumInfos;

    public AlbumAdapter(Context context, ArrayList<AlbumInfo> albumInfos) {
        this.context = context;
        this.albumInfos = albumInfos;
    }

    @Override
    public int getCount() {
        return albumInfos.size();
    }

    @Override
    public AlbumInfo getItem(int position) {
        return albumInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_item_album,parent,false);
            holder=new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        String linkApi="http://image.mp3.zdn.vn/";
        AlbumInfo albumInfo = albumInfos.get(position);
        Glide.with(context).load(linkApi+albumInfo.getCover())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageAlbum);

        holder.txtNameAlbum.setText(albumInfo.getTitle());
        holder.txtNameArtist.setText(albumInfo.getArtist());
        return convertView;
    }

    private class Holder{
        ImageView imageAlbum;
        TextView txtNameAlbum;
        TextView txtNameArtist;

        public Holder(View v) {
            this.imageAlbum = (ImageView)v.findViewById(R.id.iwImageAlbum);
            this.txtNameAlbum = (TextView)v.findViewById(R.id.txtNameAlbum);
            this.txtNameArtist = (TextView)v.findViewById(R.id.txtNameSinger);
        }
    }
}
