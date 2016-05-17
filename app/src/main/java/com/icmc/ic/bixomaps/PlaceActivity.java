package com.icmc.ic.bixomaps;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.icmc.ic.bixomaps.fragments.OnPlaceSelectedListener;
import com.icmc.ic.bixomaps.fragments.PagerFragment;
import com.icmc.ic.bixomaps.fragments.PlaceFragment;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.network.Api;

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
            //mPagerFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mPagerFragment).commit();
        }

        if (getIntent().hasExtra("TITLE")) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getIntent().getStringExtra("TITLE"));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        if (getIntent().hasExtra("CATEGORY")) {
            String category = getIntent().getStringExtra("CATEGORY");
            getRecommendations(category);
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
                                mPagerFragment.refresh();
                            }
                        }
                    });
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public MessageResponse.Place getPlace() {
        return mPlace;
    }
}
