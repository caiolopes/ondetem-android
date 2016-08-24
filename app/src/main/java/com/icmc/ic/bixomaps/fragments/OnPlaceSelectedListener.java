package com.icmc.ic.bixomaps.fragments;

import android.location.Location;

import com.icmc.ic.bixomaps.models.MessageResponse;

import java.util.List;

/**
 *
 * @author Caio Lopes
 */
public interface OnPlaceSelectedListener {
    List<MessageResponse.Place> getPlaces();
    MessageResponse.Place getPlace();
    Location getLastUsedLocation();
    void onPlaceSelected(int position);
    void setTitle(String title);
    void getRecommendations(Location location);
}
