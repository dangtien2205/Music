package dangtien.tapbi.com.music.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import dangtien.tapbi.com.music.App;
import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.activity.MainActivity;
import dangtien.tapbi.com.music.adapter.list_adapter.AlbumAdapter;
import dangtien.tapbi.com.music.mode.AlbumInfo;
import dangtien.tapbi.com.music.mode.AlbumResponse;

/**
 * Created by TienBi on 21/09/2016.
 */
public class ListAlbumFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static String ID = "id";
    private AlbumAdapter albumAdapter;
    private static final String URL1 = "http://api.mp3.zing.vn/api/mobile/playlist/getplaylistbygenre?requestdata={%22length%22:15,%22id%22:9,%22start%22:0,%22sort%22:%22total_play%22}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
    private static final String URL2 = "http://api.mp3.zing.vn/api/mobile/playlist/getplaylistbygenre?requestdata={%22length%22:15,%22id%22:9,%22start%22:0,%22sort%22:%22release_date%22}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
    private static final String URL3 = "http://api.mp3.zing.vn/api/mobile/playlist/getplaylistbygenre?requestdata={%22length%22:15,%22id%22:9,%22start%22:0,%22sort%22:%22hot%22}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
    private String ur;
    private ArrayList<AlbumInfo> albumInfos;
    private GridView gr;
    private TextView txtError;

    public ListAlbumFragment(int n) {
        switch (n) {
            case 1:
                ur = URL1;
                break;
            case 2:
                ur = URL2;
                break;
            case 3:
                ur = URL3;
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_list_album, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gr = (GridView) view.findViewById(R.id.grvAlbum);
        albumInfos = new ArrayList<>();
        albumAdapter = new AlbumAdapter(App.getContext(), albumInfos);
        txtError = (TextView)view.findViewById(R.id.txtError);
        gr.setAdapter(albumAdapter);
        gr.setOnItemClickListener(this);
        new LoadAblumTask().execute(ur);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SongFragment songFragment = new SongFragment(albumInfos.get(position).getPlaylist_id());
        ((MainActivity) getActivity()).replaceFragment(songFragment);
    }

    private class LoadAblumTask extends AsyncTask<String, Void, ArrayList<AlbumInfo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumInfos.clear();
        }

        @Override
        protected ArrayList<AlbumInfo> doInBackground(String... params) {
            try {
                Gson gson = new Gson();
                URL url = new URL(params[0]);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                AlbumResponse response = gson.fromJson(inputStreamReader, AlbumResponse.class);
                ArrayList<AlbumInfo> results = response.getAlbumInfos();
                return results;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<AlbumInfo> list) {
            super.onPostExecute(list);
            if (list==null){
                gr.setVisibility(View.GONE);
                txtError.setVisibility(View.VISIBLE);
            }else {
                albumInfos.addAll(list);
                albumAdapter.notifyDataSetChanged();
            }
        }
    }
}
