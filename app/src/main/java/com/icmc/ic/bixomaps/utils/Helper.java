package com.icmc.ic.bixomaps.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.icmc.ic.bixomaps.R;

/** Helper class with useful methods that can be used in different classes
 * @author caiolopes
 */
public class Helper {
    public static final String TAG = Helper.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Change the string in entry to be added to the request with the correct category name
     * @param entry entry
     * @param context context
     * @return menu category
     */
    public static String getCategoryField(String entry, Context context){
        if(entry.equalsIgnoreCase(context.getString(R.string.category_car))) entry = "car_services";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_bank))) entry = "bank";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_education))) entry = "education";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_medical))) entry = "medical_care";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_emergency))) entry = "emergency_services";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_culture))) entry = "culture_entertainment";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_food))) entry = "food_drink";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_government))) entry = "government";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_lodging))) entry = "lodging";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_recreation))) entry = "recreation";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_publicservices))) entry = "public_services";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_shops))) entry = "service_shops_stores";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_transport))) entry = "public_transport";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_worship))) entry = "places_worship";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_home))) entry = "basic_home_services";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_beauty))) entry = "beauty_care";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_administrative))) entry = "administrative_services";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_animal))) entry = "animal_care";
        else if(entry.equalsIgnoreCase(context.getString(R.string.category_notary))) entry = "notary_courier";
        return entry;
    }
}
