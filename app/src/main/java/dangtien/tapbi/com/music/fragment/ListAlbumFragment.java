package dangtien.tapbi.com.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
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
public class ListAlbumFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private AlbumAdapter albumAdapter;
    private ArrayList<AlbumInfo> albumList;
    private ArrayList<AlbumInfo> albumListAdd;
    private GridView gr;
    private TextView txtError;
    private int scrollStale;
    private int index;

    public ListAlbumFragment(int n) {
        scrollStale = 15;
        index = n;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    gr.setAdapter(albumAdapter);
                    if (scrollStale - 20 < 0) {
                        gr.setSelection(scrollStale - 15);
                    } else {
                        gr.setSelection(scrollStale - 20);
                    }
                    gr.setOnItemClickListener(ListAlbumFragment.this);
                    gr.setOnScrollListener(ListAlbumFragment.this);
                    break;
                case 2:
                    albumList.addAll(albumListAdd);
                    albumAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    };

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
        albumList = new ArrayList<>();
        txtError = (TextView) view.findViewById(R.id.txtError);
        getItemAlbums(16, 0, new OnGetAlbumOnlineListener() {
            @Override
            public void completed(ArrayList<AlbumInfo> itemAlbums) {
                for (int i = 0; i < itemAlbums.size(); i++) {
                    albumList.add(itemAlbums.get(i));
                }
                albumAdapter = new AlbumAdapter(App.getContext(), albumList);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void error(Exception e) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SongFragment songFragment = new SongFragment(albumList.get(position).getPlaylist_id());
        ((MainActivity) getActivity()).replaceFragment(songFragment);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (scrollStale - firstVisibleItem < 7) {
            getItemAlbums(16, scrollStale, new OnGetAlbumOnlineListener() {
                @Override
                public void completed(ArrayList<AlbumInfo> itemAlbums) {
                    albumListAdd = new ArrayList<>();
                    for (int i = 0; i < itemAlbums.size(); i++) {
                        albumListAdd.add(itemAlbums.get(i));
                    }
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }

                @Override
                public void error(Exception e) {

                }
            });
            scrollStale += 15;
        }
    }

//    private class LoadAblumTask extends AsyncTask<String, Void, ArrayList<AlbumInfo>> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            albumInfos.clear();
//        }
//
//        @Override
//        protected ArrayList<AlbumInfo> doInBackground(String... params) {
//            try {
//                Gson gson = new Gson();
//                URL url = new URL(params[0]);
//                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
//                AlbumResponse response = gson.fromJson(inputStreamReader, AlbumResponse.class);
//                ArrayList<AlbumInfo> results = response.getAlbumInfos();
//                return results;
//            } catch (Exception e) {
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<AlbumInfo> list) {
//            super.onPostExecute(list);
//            if (list == null) {
//                gr.setVisibility(View.GONE);
//                txtError.setVisibility(View.VISIBLE);
//            } else {
//                albumInfos.addAll(list);
//                albumAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    private void getItemAlbums(int length, int start, final OnGetAlbumOnlineListener listener) {
        final String link = getLink(length, start);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    URL url = new URL(link);
                    InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                    AlbumResponse response = gson.fromJson(inputStreamReader, AlbumResponse.class);
                    ArrayList<AlbumInfo> results  = response.getAlbumInfos();
                    listener.completed(results);
                } catch (IOException e) {
                    listener.error(e);
                    gr.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);
                }
            }
        }).start();

    }

    public interface OnGetAlbumOnlineListener {
        void completed(ArrayList<AlbumInfo> itemAlbums);

        void error(Exception e);
    }

    private String getLink(int length, int start) {
        String ur="";
        switch (index) {
            case 1:
                ur = "http://api.mp3.zing.vn/api/mobile/playlist/getplaylistbygenre?requestdata={%22length%22:" + length + ",%22id%22:9,%22start%22:" + start + ",%22sort%22:%22total_play%22}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
                break;
            case 2:
                ur = "http://api.mp3.zing.vn/api/mobile/playlist/getplaylistbygenre?requestdata={%22length%22:" + length + ",%22id%22:9,%22start%22:" + start + ",%22sort%22:%22release_date%22}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
                break;
            case 3:
                ur = "http://api.mp3.zing.vn/api/mobile/playlist/getplaylistbygenre?requestdata={%22length%22:" + length + ",%22id%22:9,%22start%22:" + start + ",%22sort%22:%22hot%22}&keycode=b319bd16be6d049fdb66c0752298ca30&fromvn=true";
                break;
            default:
                break;
        }
        return ur;
    }
}
