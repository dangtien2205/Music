package dangtien.tapbi.com.music.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.fragment.AlbumFragment;
import dangtien.tapbi.com.music.fragment.SongFragment;

public class MainActivity extends AppCompatActivity {
    private AlbumFragment albumFragment;
    private SongFragment songFragment;
    private boolean isBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        initControls();
    }

    private void initControls() {
        isBack = true;
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
        }
        replaceFragment(albumFragment);
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment.getClass().getName().equals("dangtien.tapbi.com.music.fragment.SongFragment"))
            isBack=false;
        else
            isBack=true;
        Fragment f= getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
        if (f!=null) {
            if (fragment.isVisible()){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_content,fragment,fragment.getClass().getName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
