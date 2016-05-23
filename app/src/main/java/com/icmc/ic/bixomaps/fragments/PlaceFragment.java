package com.icmc.ic.bixomaps.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.views.Dialogs;
import com.icmc.ic.bixomaps.views.adapters.ReviewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Place Info Fragment
 * Created by caiolopes on 5/17/16.
 */
public class PlaceFragment extends Fragment {
    private View mView;
    private OnPlaceSelectedListener mCallback;
    private RatingBar mRatingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_check:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<MessageResponse.Reviews> reviews = new ArrayList<>();
        if (mCallback.getPlace().getReviews() != null) {
            reviews.addAll(mCallback.getPlace().getReviews());
        } else {
            TextView noReviewsMsg = (TextView) mView.findViewById(R.id.no_reviews);
            noReviewsMsg.setVisibility(View.VISIBLE);
        }
        ReviewAdapter adapter = new ReviewAdapter(reviews);
        recyclerView.setAdapter(adapter);

        String name = mCallback.getPlace().getName();
        String address = mCallback.getPlace().getAddress();
        String phone = mCallback.getPlace().getPhone();
        String website = mCallback.getPlace().getWebsite();
        float rating = mCallback.getPlace().getRating();

        String placeInfo =
                "<p><b>Nome:</b> " + name + "</p>"
                        + "<p><b>Endereço:</b> " + address + "</p>";
        if (phone != null)
            if (!phone.isEmpty())
                placeInfo = placeInfo.concat("<p><b>Telefone:</b> " + phone + "</p>");
        if (website != null)
            if (!website.isEmpty())
                placeInfo = placeInfo.concat("<p><b>Website:</b> " + website + "</p>");
        placeInfo = placeInfo.concat("<b>Nota:</b> " + rating);

        TextView placeInfotextView = (TextView) mView.findViewById(R.id.place_info);
        assert placeInfotextView != null;
        placeInfotextView.setText(Html.fromHtml(placeInfo));
        RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.rating);
        ratingBar.setRating(rating);
        mRatingBar = (RatingBar) mView.findViewById(R.id.rate_place);
        setRatingBarListener();
    }

    public void sendReview(String comment, float rating) {

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
