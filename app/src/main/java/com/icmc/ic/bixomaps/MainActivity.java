package com.icmc.ic.bixomaps;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.icmc.ic.bixomaps.fragments.MapFragment;
import com.icmc.ic.bixomaps.utils.Helper;
import com.icmc.ic.bixomaps.views.adapters.MenuAdapter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Handles all interaction with the Google API for using GPS location, permissions
 * request for using GPS, callbacks, etc. It also has tne navigation drawer menu.
 *
 * @author caiolopes
 */
public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    private final static int REQUEST_LOCATION = 199;
    private boolean mRequestingLocationUpdates;
    public MapFragment mFragment;
    public Location mLastLocation;
    public MenuAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);

        requestLocationPermission(this);

        setupLocationServices();

        createDrawerMenu();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mFragment = new MapFragment();
        fragmentTransaction.add(R.id.main_layout, mFragment);
        fragmentTransaction.commit();
    }

    /**
     * Creating drawer menu
     */
    void createDrawerMenu() {
        Map<String, Integer> mMenuList = new TreeMap<>();
        mMenuList.put(getString(R.string.category_car), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_bank), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_education), R.drawable.ic_menu_education_vector);
        mMenuList.put(getString(R.string.category_medical), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_emergency), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_culture), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_food), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_government), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_lodging), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_recreation), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_publicservices), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_shops), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_transport), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_worship), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_home), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_beauty), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_administrative), R.drawable.ic_menu_restaurant_vector);
        mMenuList.put(getString(R.string.category_animal), R.drawable.ic_menu_restaurant_vector);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menu_list);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        mAdapter = new MenuAdapter(this, mMenuList);
        recyclerView.setAdapter(mAdapter);
    }

    protected void onStart() {
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
        super.onStop();
    }

    void setupLocationServices() {
        if (Helper.checkPlayServices(this)) {
            mRequestingLocationUpdates = true;
            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            createLocationRequest();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (Helper.checkPermission(this)) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                mFragment.updateMap(mLastLocation);
            }
            if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation == null) {
            mFragment.updateMap(location);
        }

        mLastLocation = location;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    protected void startLocationUpdates() {
        if (Helper.checkPermission(this))
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            MainPresenter.addDialog(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to check if location permission is granted
     * @return if location permission is granted
     */
    public static boolean requestLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            int hasWriteContactsPermission = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);
                return false;
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Location Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Log.v(TAG, "Permission granted!!");
                    startLocationUpdates();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
