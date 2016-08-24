package com.icmc.ic.bixomaps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.views.adapters.PlaceAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * List of places.
 * Created by caiolopes on 5/15/16.
 */
public class PlacesListFragment extends Fragment {
    public static final String TAG = PlacesListFragment.class.getSimpleName();
    private View mView;
    private PlaceAdapter mAdapter;
    private OnPlaceSelectedListener mCallback;
    private RecyclerView mRecyclerView;
    private Integer mSortMethod = 0;
    private Integer mRecommendationsSize = 15;

    public static PlacesListFragment newInstance() {

        Bundle args = new Bundle();

        PlacesListFragment fragment = new PlacesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_places_list, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.places_list);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        List<MessageResponse.Place> mPlaces = new ArrayList<>();
        mPlaces.addAll(mCallback.getPlaces());
        getPreferences();
        mAdapter = new PlaceAdapter(getActivity(), mPlaces, mSortMethod);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getPreferences() {
        mSortMethod = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sort_method", "0"));
        mRecommendationsSize = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("recommendations_size", "15"));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null) {
            if (isVisibleToUser) {
                if (getActivity().findViewById(android.R.id.content).getParent() instanceof View) {
                    View v = (View) getActivity().findViewById(android.R.id.content).getParent();
                    AppBarLayout appBarLayout = (AppBarLayout) v.findViewById(R.id.app_bar);
                    if (appBarLayout != null) {
                        appBarLayout.setExpanded(true, true);
                        refresh();
                    }
                }
            }
        }
    }

    public void refresh() {
        if (mView != null) {
            getPreferences();
            TextView noPlacesErrorMsg = (TextView) mView.findViewById(R.id.no_places_message);
            List<MessageResponse.Place> places = mCallback.getPlaces();
            if (places.size() == 0) {
                noPlacesErrorMsg.setVisibility(View.VISIBLE);
            } else {
                Collections.sort(places, new Comparator<MessageResponse.Place>() {
                    @Override
                    public int compare(MessageResponse.Place place, MessageResponse.Place t1) {
                        switch (mSortMethod) {
                            case 1:
                                return place.getRating() < t1.getRating() ? 1
                                        : place.getRating() > t1.getRating() ? -1 : 0;
                            case 2:
                                return place.getReviews().size() < t1.getReviews().size() ? 1
                                        : place.getReviews().size() > t1.getReviews().size() ? -1 : 0;
                            default:
                            return place.getDistance() < t1.getDistance() ? -1
                                    : place.getDistance() > t1.getDistance() ? 1 : 0;
                        }
                    }
                });
                while (places.size() > mRecommendationsSize) places.remove(places.size()-1);
                noPlacesErrorMsg.setVisibility(View.GONE);
                mAdapter.animateTo(places);
                mRecyclerView.scrollToPosition(0);
            }
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
}
