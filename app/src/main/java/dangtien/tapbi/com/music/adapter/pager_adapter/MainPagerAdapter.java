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
                MainFragment f1= new MainFragment(1);
                return f1;
            case 1:
                MainFragment f2=new MainFragment(2);
                return f2;
            case 2:
                MainFragment f3=new MainFragment(3);
                return f3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="Nổi Bật";
                break;
            case 1:
                title="Mới Nhất";
                break;
            case 2:
                title="Nghe Nhiều";
                break;
        }
        return title;
    }
}
