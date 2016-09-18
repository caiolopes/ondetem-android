package com.icmc.ic.bixomaps;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;

import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Category Activity.
 * @author Caio Lopes
 */
public class PlaceActivity extends AppBaseActivity implements OnPlaceSelectedListener {
    public static final String TAG = PlaceActivity.class.getSimpleName();
    private List<MessageResponse.Place> mPlaces;
    private MessageResponse.Place mPlace;
    private PagerFragment mPagerFragment;
    private String mCategory;
    private Location mLastUsedLocation = mLastLocation;
    private boolean mCalled = false;
    private Subscription networkConnectivitySubscription;
    private Subscription recommendationsSubscription;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) mLastUsedLocation = null;

        if (getIntent().hasExtra("CATEGORY")) {
            mCategory = getIntent().getStringExtra("CATEGORY");
        }

        networkConnectivitySubscription = ReactiveNetwork.observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Connectivity>() {
                    @Override public void call(Connectivity connectivity) {
                        if (connectivity.getState() == NetworkInfo.State.CONNECTED) {
                            mPagerFragment.refreshing();
                            getRecommendations(null);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        safelyUnsubscribe(networkConnectivitySubscription, recommendationsSubscription);
    }



    private void safelyUnsubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setEnabled(false).setVisible(false);
        menu.findItem(R.id.action_search).setEnabled(false).setVisible(false);
        return true;
    }

    @Override
    protected void locationUpdate(Location location) {
        if (mCalled && mPlaces.size() == 0) {
            mPagerFragment.refreshing();
            getRecommendations(null);
        }
    }

    @Override
    void userLocationNotAllowed() {
        mPagerFragment.refresh(null);
    }

    @Override
    public Location getLastUsedLocation() {
        return mLastUsedLocation;
    }

    /**
     * Method that interacts with API to get the all the recommendations of this category.
     * @param location location
     */
    @Override
    public void getRecommendations(final Location location) {
        mLastUsedLocation = location == null ? AppBaseActivity.mLastLocation : location;
        mPlaces.clear();
        if (mLastUsedLocation != null && mCategory != null) {
            Api presenter = new Api();
            recommendationsSubscription = presenter.getRecommendations(mLastUsedLocation, mCategory).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                            mPagerFragment.refresh(mLastUsedLocation);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mPagerFragment.refresh(mLastUsedLocation);
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
                                for (MessageResponse.Place place : mPlaces) {
                                    Location location = new Location("Place Location");
                                    location.setLatitude(Double.parseDouble(place.getLat()));
                                    location.setLongitude(Double.parseDouble(place.getLong()));
                                    place.setDistance(location.distanceTo(mLastLocation));
                                }
                            }
                        }
                    });
        } else
            mCalled = true;
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
    public BitmapDescriptor getCategoryIcon() {
        switch(mCategory) {
            case "car_services": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_car);
            case "bank": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_creditcard);
            case "education": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_book);
            case "medical_care": return BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital);
            case "emergency_services": return BitmapDescriptorFactory.fromResource(R.drawable.ic_emergency);
            case "culture_entertainment": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_music_1);
            case "food_drink": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_restaurant);
            case "government": return BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance);
            case "lodging": return BitmapDescriptorFactory.fromResource(R.drawable.ic_hotel);
            case "recreation": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_bike);
            case "public_services": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_business);
            case "service_shops_stores": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_cart);
            case "public_transport": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_bus);
            case "places_worship": return BitmapDescriptorFactory.fromResource(R.drawable.ic_church);
            case "basic_home_services": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_home);
            case "beauty_care": return BitmapDescriptorFactory.fromResource(R.drawable.ic_beauty);
            case "administrative_services": return BitmapDescriptorFactory.fromResource(R.drawable.ic_action_stamp);
            case "animal_care": return BitmapDescriptorFactory.fromResource(R.drawable.ic_pets);
            default: return null;
        }
    }

    @Override
    public MessageResponse.Place getPlace() {
        return mPlace;
    }
}
