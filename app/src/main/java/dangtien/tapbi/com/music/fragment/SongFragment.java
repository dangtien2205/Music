package dangtien.tapbi.com.music.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.adapter.list_adapter.SongAdapter;
import dangtien.tapbi.com.music.mode.SongInfo;
import dangtien.tapbi.com.music.mode.SongResponse;

public class SongFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String idAlbum;
    private ImageView imageAlbum;
    private ArrayList<SongInfo> songInfos;
    private SongAdapter songAdapter;
    private ListView lvSong;

    public SongFragment(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_song,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Intent intent = getIntent();
//        idAlbum = intent.getStringExtra(ListAlbumFragment.ID);
        imageAlbum = (ImageView)view.findViewById(R.id.iwImageAlbum1);
        songInfos = new ArrayList<>();
        songAdapter = new SongAdapter(songInfos,getContext());
        lvSong=(ListView) view.findViewById(R.id.lvListSong);
        lvSong.setAdapter(songAdapter);
        lvSong.setOnItemClickListener(this);
        String url = "http://api.mp3.zing.vn/api/mobile/playlist/getsonglist?requestdata={%22length%22:200,%22id%22:%22"
                +idAlbum+"%22,%22start%22:0}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
        new LoadListMusicTask().execute(url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        (view.findViewById(R.id.layoutPlay)).setVisibility(View.VISIBLE);
    }

    private class LoadListMusicTask extends AsyncTask<String,Void,ArrayList<SongInfo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            songInfos.clear();
        }

        @Override
        protected ArrayList<SongInfo> doInBackground(String... params) {
            try {
                Gson gson = new Gson();
                URL url = new URL(params[0]);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(),"UTF-8");
                SongResponse response = gson.fromJson(inputStreamReader,SongResponse.class);
                ArrayList<SongInfo> results = response.getSongInfos();
                return results;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SongInfo> list) {
            super.onPostExecute(list);
            songInfos.addAll(list);
            String linkApi="http://image.mp3.zdn.vn/";
            Glide.with(SongFragment.this).load(linkApi+songInfos.get(0).getThumbnail())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageAlbum);
            songAdapter.notifyDataSetChanged();
        }
    }
}
