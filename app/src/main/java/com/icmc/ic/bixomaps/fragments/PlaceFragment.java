package com.icmc.ic.bixomaps.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icmc.ic.bixomaps.AppBaseActivity;
import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.EventRequest;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.network.Api;
import com.icmc.ic.bixomaps.utils.Helper;
import com.icmc.ic.bixomaps.views.Dialogs;
import com.icmc.ic.bixomaps.views.adapters.ReviewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Place Info Fragment
 * Created by caiolopes on 5/17/16.
 */
public class PlaceFragment extends Fragment implements OnMapReadyCallback {
    private View mView;
    private OnPlaceSelectedListener mCallback;
    private RatingBar mRatingBar;
    private ReviewAdapter mAdapter;
    private List<MessageResponse.Reviews> mReviews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.place, menu);
        Location location = new Location("place");
        location.setLatitude(Double.parseDouble(mCallback.getPlace().getLat()));
        location.setLongitude(Double.parseDouble(mCallback.getPlace().getLong()));
        if (AppBaseActivity.mLastLocation.distanceTo(location) > 50) {
            menu.removeItem(R.id.action_check);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_check:
                checkIn();
                return true;
            case R.id.action_directions:
                routeEvent();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+AppBaseActivity.mLastLocation.getLatitude()+","
                                +AppBaseActivity.mLastLocation.getLongitude()
                                +"&daddr="+mCallback.getPlace().getLat()
                                +","+mCallback.getPlace().getLong()));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void checkIn() {
        Api presenter = new Api();
        presenter.sendEvent(mCallback.getPlace().getId(),
                mCallback.getPlace().getCategory(),
                EventRequest.Event.CHECK_IN,
                null,
                null,
                AppBaseActivity.mLastLocation.getLatitude(),
                AppBaseActivity.mLastLocation.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<ResponseBody>>() {
                    @Override
                    public void call(Response<ResponseBody> response) {
                        Toast.makeText(getContext(), getString(R.string.check_in_sent), Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void clickEvent() {
        Api presenter = new Api();
        presenter.sendEvent(mCallback.getPlace().getId(),
                mCallback.getPlace().getCategory(),
                EventRequest.Event.CLICK,
                null,
                null,
                AppBaseActivity.mLastLocation.getLatitude(),
                AppBaseActivity.mLastLocation.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void routeEvent() {
        Api presenter = new Api();
        presenter.sendEvent(mCallback.getPlace().getId(),
                mCallback.getPlace().getCategory(),
                EventRequest.Event.DIRECTION,
                null,
                null,
                AppBaseActivity.mLastLocation.getLatitude(),
                AppBaseActivity.mLastLocation.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_place, container, false);
        return mView;
    }

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCallback.setTitle(mCallback.getPlace().getName());

        if (getActivity().findViewById(android.R.id.content).getParent() instanceof View) {
            View v = (View) getActivity().findViewById(android.R.id.content).getParent();
            AppBarLayout appBarLayout = (AppBarLayout) v.findViewById(R.id.app_bar);
            if (appBarLayout != null) {
                appBarLayout.setExpanded(true, true);
            }
        }

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.reviews_list);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        mReviews = new ArrayList<>();
        if (mCallback.getPlace().getReviews().size() > 0) {
            mReviews.addAll(mCallback.getPlace().getReviews());
        } else {
            TextView reviewsHeader = (TextView) mView.findViewById(R.id.reviews_header);
            reviewsHeader.setText(getString(R.string.no_reviews));
        }
        mAdapter = new ReviewAdapter(mReviews);
        recyclerView.setAdapter(mAdapter);

        ((TextView)mView.findViewById(R.id.place_name)).setText(mCallback.getPlace().getName());
        ((TextView)mView.findViewById(R.id.place_phone)).setText(mCallback.getPlace().getPhone());
        ((TextView)mView.findViewById(R.id.place_address)).setText(mCallback.getPlace().getAddress());
        ((TextView)mView.findViewById(R.id.place_website)).setText(mCallback.getPlace().getWebsite());
        ((TextView)mView.findViewById(R.id.place_rating))
                .setText(String.format(Locale.getDefault(), "%.02f", mCallback.getPlace().getRating()));

        RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.rating);
        ratingBar.setRating(mCallback.getPlace().getRating());
        mRatingBar = (RatingBar) mView.findViewById(R.id.rate_place);
        setRatingBarListener();
        clickEvent();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.map_place, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    public void sendReview(String comment, float rating) {
        Api presenter = new Api();
        final MessageResponse.Reviews review = new MessageResponse.Reviews();
        review.setTime(String.valueOf(new Date().getTime()/1000L));
        review.setText(comment);
        review.setOverall_rating(String.valueOf(rating));
        presenter.sendEvent(mCallback.getPlace().getId(),
                mCallback.getPlace().getCategory(),
                EventRequest.Event.REVIEW,
                rating,
                comment,
                AppBaseActivity.mLastLocation.getLatitude(),
                AppBaseActivity.mLastLocation.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<ResponseBody>>() {
                    @Override
                    public void call(Response<ResponseBody> response) {
                        Toast.makeText(getContext(), getString(R.string.review_sent), Toast.LENGTH_LONG)
                                .show();
                        mReviews.add(review);
                        mAdapter.notifyItemInserted(mReviews.size()-1);
                        TextView reviewsHeader = (TextView) mView.findViewById(R.id.reviews_header);
                        reviewsHeader.setText(getString(R.string.reviews_title));
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        if(Helper.checkPermission(getActivity())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(mCallback.getPlace().getLat()),
                            Double.parseDouble(mCallback.getPlace().getLong())))
                    .title(mCallback.getPlace().getName()));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(marker.getPosition());
            builder.include(new LatLng(AppBaseActivity.mLastLocation.getLatitude(),
                    AppBaseActivity.mLastLocation.getLongitude()));
            LatLngBounds bounds = builder.build();
            int padding = 50;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
        }
    }

    public void setRatingBarListener() {
        mRatingBar.setOnRatingBarChangeListener(null);
        mRatingBar.setRating(0);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Dialogs.review(PlaceFragment.this, rating);
            }
        });
    }
}
