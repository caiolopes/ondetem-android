package com.icmc.ic.bixomaps.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.MessageResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 *
 * Created by caiolopes on 5/22/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private List<MessageResponse.Reviews> reviews;

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        TextView reviewText;
        TextView reviewDate;
        RatingBar reviewRating;

        public ReviewHolder(View itemView) {
            super(itemView);
            reviewText = (TextView) itemView.findViewById(R.id.review_text);
            reviewDate = (TextView) itemView.findViewById(R.id.review_date);
            reviewRating = (RatingBar) itemView.findViewById(R.id.review_rating);
        }
    }

    public ReviewAdapter(List<MessageResponse.Reviews> reviews) {
        this.reviews = reviews;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        MessageResponse.Reviews review = reviews.get(position);
        holder.reviewText.setText(review.getText());

        Calendar time = Calendar.getInstance();
        try {
            time.setTimeInMillis(Long.parseLong(review.getTime()) * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            holder.reviewDate.setText(sdf.format(time.getTime()));
        } catch (NumberFormatException e) {
            holder.reviewDate.setText(review.getTime());
        }
        if (review.getOverall_rating() != null)
            holder.reviewRating.setRating(Float.parseFloat(review.getOverall_rating()));
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
