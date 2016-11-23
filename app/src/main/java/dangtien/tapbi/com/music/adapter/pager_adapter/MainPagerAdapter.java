package dangtien.tapbi.com.music.adapter.pager_adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dangtien.tapbi.com.music.fragment.MainFragment;

/**
 * Created by TienBi on 21/09/2016.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MainFragment f1= new MainFragment();
                return f1;
            case 1:
                MainFragment f2=new MainFragment();
                return f2;
            case 2:
                MainFragment f3=new MainFragment();
                return f3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
