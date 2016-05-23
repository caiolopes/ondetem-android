package com.icmc.ic.bixomaps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icmc.ic.bixomaps.AppBaseActivity;
import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.views.adapters.PlaceAdapter;

/**
 * List of places.
 * Created by caiolopes on 5/15/16.
 */
public class PlacesListFragment extends Fragment {
    public static final String TAG = PlacesListFragment.class.getSimpleName();
    private View mView;
    private PlaceAdapter mAdapter;
    private OnPlaceSelectedListener mCallback;
    private boolean mError;

    public static PlacesListFragment newInstance() {

        Bundle args = new Bundle();

        PlacesListFragment fragment = new PlacesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setError(boolean error) {
        mError = error;
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

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.places_list);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        mAdapter = new PlaceAdapter(getActivity(), mCallback.getPlaces(), AppBaseActivity.mLastLocation);
        recyclerView.setAdapter(mAdapter);
        refresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity().findViewById(android.R.id.content).getParent() instanceof View) {
                View v = (View) getActivity().findViewById(android.R.id.content).getParent();
                AppBarLayout appBarLayout = (AppBarLayout) v.findViewById(R.id.app_bar);
                if (appBarLayout != null) {
                    appBarLayout.setExpanded(true, true);
                }
            }
        }
    }

    public void refresh() {
        TextView noPlacesErrorMsg = (TextView) mView.findViewById(R.id.no_places_message);
        if (mError) {
            noPlacesErrorMsg.setVisibility(View.VISIBLE);
        } else {
            noPlacesErrorMsg.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
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
