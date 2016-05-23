package com.icmc.ic.bixomaps.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.icmc.ic.bixomaps.AppBaseActivity;
import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.utils.Helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the Google Map visualization, callbacks, etc.
 * @author caiolopes
 */
public class PlacesMapFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = PlacesMapFragment.class.getSimpleName();
    private OnPlaceSelectedListener mCallback;
    private GoogleMap mMap;
    private Map<Marker, Integer> mMarkerMap;

    public static PlacesMapFragment newInstance() {

        Bundle args = new Bundle();

        PlacesMapFragment fragment = new PlacesMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity().findViewById(android.R.id.content).getParent() instanceof View) {
                View v = (View) getActivity().findViewById(android.R.id.content).getParent();
                AppBarLayout appBarLayout = (AppBarLayout) v.findViewById(R.id.app_bar);
                if (appBarLayout != null) {
                    appBarLayout.setExpanded(false, true);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
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
            refresh();

            // Set a listener for info window events.
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    mCallback.onPlaceSelected(mMarkerMap.get(marker));
                }
            });
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

    public void refresh() {
        final Location mLastLocation = AppBaseActivity.mLastLocation;

        if (mLastLocation != null) {
            mMap.clear();
            mMarkerMap = new HashMap<>();
            int i = 0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MessageResponse.Place p : mCallback.getPlaces()) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(p.getLat()),
                                Double.parseDouble(p.getLong())))
                        .title(p.getName()));
                marker.setSnippet(p.getAddress());
                builder.include(marker.getPosition());
                mMarkerMap.put(marker, i);
                i++;
            }
            // Include user position
            builder.include(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            LatLngBounds bounds = builder.build();
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
        }
    }

    public void updateMap(Location location) {
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 15f));
    }
}
