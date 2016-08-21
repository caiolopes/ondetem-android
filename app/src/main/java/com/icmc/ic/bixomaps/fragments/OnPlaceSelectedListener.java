package com.icmc.ic.bixomaps.fragments;

import android.location.Location;

import com.icmc.ic.bixomaps.models.MessageResponse;

import java.util.List;

/**
 *
 * Created by caiolopes on 5/17/16.
 */
public interface OnPlaceSelectedListener {
    List<MessageResponse.Place> getPlaces();
    MessageResponse.Place getPlace();
    void onPlaceSelected(int position);
    void setTitle(String title);
    void getRecommendations(Location location);
}
