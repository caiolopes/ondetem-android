package com.icmc.ic.bixomaps;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.icmc.ic.bixomaps.fragments.MapFragment;

/**
 * Category Activity for showing ViewPager with the list and the map tabs.
 * Created by caiolopes on 5/14/16.
 */
public class CategoryActivity extends AppBaseActivity {
    public static final String TAG = CategoryActivity.class.getSimpleName();
    public static final int NUM_PAGES = 1;
    private ViewPager mViewPager;
    private String mCategory;
    MapFragment mMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);

        // Setup View Pager
        assert mViewPager != null;
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mCategory = null;
        if(getIntent().hasExtra("CATEGORY")) {
            mCategory = getIntent().getStringExtra("CATEGORY");
            mMapFragment = MapFragment.newInstance(mCategory);
        }
        if (getIntent().hasExtra("TITLE")) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getIntent().getStringExtra("TITLE"));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    protected void locationUpdate(Location location) {
        Log.v(TAG, "Location Update!");
        mMapFragment.updateMap(location);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (mCategory != null)
                return mMapFragment;

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
