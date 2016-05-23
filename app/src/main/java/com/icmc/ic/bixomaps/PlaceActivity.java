package com.icmc.ic.bixomaps;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.icmc.ic.bixomaps.fragments.OnPlaceSelectedListener;
import com.icmc.ic.bixomaps.fragments.PagerFragment;
import com.icmc.ic.bixomaps.fragments.PlaceFragment;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.network.Api;
import com.icmc.ic.bixomaps.views.Dialogs;

import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Category Activity.
 * Created by caiolopes on 5/14/16.
 */
public class PlaceActivity extends AppBaseActivity implements OnPlaceSelectedListener {
    public static final String TAG = PlaceActivity.class.getSimpleName();
    private List<MessageResponse.Place> mPlaces;
    private MessageResponse.Place mPlace;
    private PagerFragment mPagerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mPlaces = new ArrayList<>();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            mPagerFragment = new PagerFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mPagerFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.add(R.id.fragment_container, mPagerFragment).commit();
        }

        if (getIntent().hasExtra("CATEGORY")) {
            String category = getIntent().getStringExtra("CATEGORY");
            getRecommendations(category);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                Dialogs.addPlace(this);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void locationUpdate(Location location) {
        //Log.d(TAG, "Location Update!");
    }

    /**
     * Method that interacts with API to get the all the recommendations of this category.
     * @param category category name
     */
    public void getRecommendations(String category) {
        final Location mLastLocation = AppBaseActivity.mLastLocation;

        if (mLastLocation != null && category != null) {
            Api presenter = new Api();
            presenter.getRecommendations(mLastLocation, category).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            mPagerFragment.setError(true);
                        }

                        @Override
                        public void onNext(ResponseBody messageResponse) {
                            MessageResponse response = null;
                            try {
                                String ret = new String(messageResponse.bytes(), "ISO-8859-1");
                                Persister persister = new Persister();
                                response = persister.read(MessageResponse.class, ret);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading and converting response", e);
                            }

                            if (response != null) {
                                mPlaces.addAll(response.getReply().getRecommendations());
                                mPagerFragment.setError(false);
                                mPagerFragment.refresh();
                            }
                        }
                    });
        } else {
            mPagerFragment.setError(true);
        }
    }

    @Override
    public List<MessageResponse.Place> getPlaces() {
        return mPlaces;
    }

    @Override
    public void onPlaceSelected(int position) {
        mPlace = mPlaces.get(position);

        // Create new fragment and transaction
        Fragment newFragment = new PlaceFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        ft.replace(R.id.fragment_container, newFragment);
        ft.addToBackStack(null);

        // Commit the transaction
        ft.commit();
    }

    @Override
    public void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public MessageResponse.Place getPlace() {
        return mPlace;
    }
}
