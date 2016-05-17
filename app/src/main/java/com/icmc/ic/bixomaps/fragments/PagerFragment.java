package com.icmc.ic.bixomaps.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icmc.ic.bixomaps.R;

/**
 * View Pager Fragment
 * Created by caiolopes on 5/16/16.
 */
public class PagerFragment extends Fragment {
    public static final int NUM_PAGES = 2;
    private View mView;
    private ViewPager mViewPager;
    private PlacesMapFragment mPlacesMapFragment;
    private PlacesListFragment mPlacesListFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pager, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup View Pager
        assert mViewPager != null;
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);
        mPlacesMapFragment = PlacesMapFragment.newInstance();
        mPlacesListFragment = PlacesListFragment.newInstance();
    }

    public void refresh() {
        mPlacesListFragment.refresh();
        mPlacesMapFragment.refresh();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return mPlacesListFragment;
            else if (position == 1)
                return mPlacesMapFragment;

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getString(R.string.list_tab);
            else if (position == 1)
                return getString(R.string.map_tab);

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}