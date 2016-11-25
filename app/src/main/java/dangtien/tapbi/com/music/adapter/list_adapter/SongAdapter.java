package dangtien.tapbi.com.music.adapter.list_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.mode.SongInfo;

/**
 * Created by toannt on 25/11/2016.
 */

public class SongAdapter extends BaseAdapter {
    private ArrayList<SongInfo> songInfos;
    private Context context;

    public SongAdapter(ArrayList<SongInfo> songInfos, Context context) {
        this.songInfos = songInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return songInfos.size();
    }

    @Override
    public SongInfo getItem(int position) {
        return songInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_item_song,parent,false);
            holder=new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        SongInfo songInfo = songInfos.get(position);
        holder.txtNameSong.setText(songInfo.getTitle());
        holder.txtNameSinger.setText(songInfo.getArtist());
        return convertView;
    }
    private class Holder{
        TextView txtNameSong;
        TextView txtNameSinger;

        public Holder(View v) {
            this.txtNameSong = (TextView)v.findViewById(R.id.txtNameSong);
            this.txtNameSinger = (TextView)v.findViewById(R.id.txtNameArtist);
        }
    }
}
