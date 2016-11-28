package dangtien.tapbi.com.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dangtien.tapbi.com.music.R;
import dangtien.tapbi.com.music.adapter.pager_adapter.MainPagerAdapter;

/**
 * Created by toannt on 28/11/2016.
 */

public class AlbumFragment extends Fragment {
    private ViewPager viewPager;
    private MainPagerAdapter mainPagerAdapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_album,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.vqMain);
        mainPagerAdapter = new MainPagerAdapter (getChildFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(mainPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}