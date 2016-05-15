package com.icmc.ic.bixomaps.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icmc.ic.bixomaps.MainActivity;
import com.icmc.ic.bixomaps.MainPresenter;
import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.utils.Helper;

import org.simpleframework.xml.core.Persister;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Handles the Google Map visualization, callbacks, etc.
 * @author caiolopes
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    public static final String TAG = MapFragment.class.getSimpleName();
    private View mView;
    private PopupWindow mPopupWindow;
    private GoogleMap mMap;
    private MainPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mPresenter = new MainPresenter();

        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(Helper.checkPermission(getActivity())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (mPopupWindow != null) {
                        if (mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                        }
                    }
                }
            });

            // Set a listener for info window events.
            mMap.setOnInfoWindowClickListener(this);
            // Supporting MultiLine for the snippet in the info window
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }
    }

    /**
     * Method that interacts with the presenter which calls the API to get the recommendations.
     * @param category category name
     * @param position position in the drawer menu
     */
    public void getRecommendations(String category, final int position) {
        final Location mLastLocation = ((MainActivity)getActivity()).mLastLocation;
        if (mLastLocation != null && category != null) {
            mPresenter.getRecommendations(mLastLocation, category).subscribeOn(Schedulers.io())
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
                                mMap.clear();
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (MessageResponse.Place p : response.getReply().getRecommendations()) {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(p.getLat()),
                                                    Double.parseDouble(p.getLong())))
                                            .title(p.getName()));
                                    marker.setSnippet(p.getAddress());
                                    builder.include(marker.getPosition());
                                }
                                // Include user position
                                builder.include(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                                LatLngBounds bounds = builder.build();
                                int padding = 100;
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.animateCamera(cu);
                                ((MainActivity) getActivity()).mAdapter.notifyItemChanged(position);
                                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                                if (drawer.isDrawerOpen(GravityCompat.START))
                                    drawer.closeDrawer(GravityCompat.START);
                            }
                        }
                    });
        }
    }

    public void updateMap(Location location) {
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 15f));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        View popupView = getLayoutInflater(null).inflate(R.layout.popup_window_info, null);

        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 10, 10);
            }
        } else {
            mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 10, 10);
        }
    }
}
