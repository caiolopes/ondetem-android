package com.icmc.ic.bixomaps.fragments;

import android.content.Context;
import android.location.Location;
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
 * @author Caio Lopes
 */
public class PagerFragment extends Fragment {
    public static final int NUM_PAGES = 2;
    private View mView;
    private ViewPager mViewPager;
    private PlacesMapFragment mPlacesMapFragment;
    private PlacesListFragment mPlacesListFragment;
    private String mTitle;
    private OnPlaceSelectedListener mCallback;
    private boolean mError = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPlaceSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPlaceSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pager, container, false);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getArguments().getString("TITLE");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCallback.setTitle(mTitle);

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

    public void refresh(Location location) {
        mPlacesListFragment.refresh();
        mPlacesMapFragment.refresh(location);
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
