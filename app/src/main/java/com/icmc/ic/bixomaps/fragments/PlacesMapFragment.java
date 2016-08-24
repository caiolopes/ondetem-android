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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private MapView mMapView;
    private GoogleMap mMap;
    private Map<Marker, Integer> mMarkerMap;
    private Integer mCameraIdle = 0;
    private Marker lastOpenned = null;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.map);
        if (mMapView != null) {
            // Initialise the MapView
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            // Set the map ready callback to receive the GoogleMap object
            mMapView.getMapAsync(this);
            mCameraIdle = 0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
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
        MapsInitializer.initialize(getContext());
        mMap = googleMap;

        if(Helper.checkPermission(getActivity())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    if (isVisible()) {
                        if (mCameraIdle > 0 && mCallback.getLastUsedLocation() != null) {
                            Location location = new Location("place");
                            location.setLatitude(mMap.getCameraPosition().target.latitude);
                            location.setLongitude(mMap.getCameraPosition().target.longitude);
                            if (location.distanceTo(mCallback.getLastUsedLocation()) > 100) {
                                mMap.addMarker(
                                        new MarkerOptions().title(getString(R.string.recommendation_point))
                                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                );
                                updateMap(location, mMap);
                                mCallback.getRecommendations(location);
                            }
                        }
                        mCameraIdle++;
                    }
                }
            });

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

            // Since we are consuming the event this is necessary to
            // manage closing openned markers before openning new ones

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Check if there is an open info window
                    if (lastOpenned != null) {
                        // Close the info window
                        lastOpenned.hideInfoWindow();

                        // Is the marker the same marker that was already open
                        if (lastOpenned.equals(marker)) {
                            // Nullify the lastOpenned object
                            lastOpenned = null;
                            // Return so that the info window isn't openned again
                            return true;
                        }
                    }

                    // Open the info window for the marker
                    marker.showInfoWindow();
                    // Re-assign the last openned such that we can close it later
                    lastOpenned = marker;

                    // Event was handled by our code do not launch default behaviour.
                    return true;
                }
            });

            updateMap(mCallback.getLastUsedLocation(), googleMap);
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

    public void refresh(Location location) {
        updateMap(location, mMap);
    }

    public void updateMap(Location location, GoogleMap map) {
        if (location != null && map != null) {
            map.clear();
            mMarkerMap = new HashMap<>();
            int i = 0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MessageResponse.Place p : mCallback.getPlaces()) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(p.getLat()),
                                Double.parseDouble(p.getLong())))
                        .title(p.getName()));
                marker.setSnippet(p.getAddress());
                builder.include(marker.getPosition());
                mMarkerMap.put(marker, i);
                i++;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(
                    new MarkerOptions().title(getString(R.string.recommendation_point))
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            );
            if (mCameraIdle == 0) {
                int padding = 100;
                // Include recommendation point
                builder.include(latLng);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.moveCamera(cu);
            }
        }
    }
}
