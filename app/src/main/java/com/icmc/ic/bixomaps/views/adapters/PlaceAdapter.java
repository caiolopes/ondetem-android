package com.icmc.ic.bixomaps.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.fragments.OnPlaceSelectedListener;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.views.ItemClickListener;

import java.util.List;

/**
 * Place list Adapter
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    public static final String TAG = PlaceAdapter.class.getSimpleName();
    private Context mContext;
    private List<MessageResponse.Place> mPlaces;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;
        TextView distance;
        TextView reviews;
        ImageView phone;
        RatingBar ratingBar;
        View view;
        ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.place_name);
            phone = (ImageView) itemView.findViewById(R.id.phone);
            distance = (TextView) itemView.findViewById(R.id.place_distance);
            reviews = (TextView) itemView.findViewById(R.id.place_reviews);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            phone.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getLayoutPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getLayoutPosition(), true);
            return true;
        }
    }

    public PlaceAdapter(Context context, List<MessageResponse.Place> places) {
        mContext = context;
        mPlaces = places;
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    @Override
    public void onBindViewHolder(final PlaceAdapter.ViewHolder holder, int position) {
        final MessageResponse.Place place = mPlaces.get(position);
        holder.name.setText(place.getName());
        holder.ratingBar.setRating(place.getRating());

        if (place.getReviews().size() == 0)
            holder.reviews.setText(mContext.getString(R.string.zero_review));
        else if (place.getReviews().size() == 1)
            holder.reviews.setText(mContext.getString(R.string.one_review, place.getReviews().size()));
        else
            holder.reviews.setText(mContext.getString(R.string.many_reviews, place.getReviews().size()));

        if (place.getDistance() < 1000) {
            holder.distance.setText(mContext.getString(R.string.distance_m, place.getDistance()));
        } else {
            holder.distance.setText(mContext.getString(R.string.distance_km, place.getDistance()/1000));
        }

        if (place.getPhone().length() > 0) {
            holder.phone.setVisibility(View.VISIBLE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (mContext instanceof OnPlaceSelectedListener && !isLongClick && view.getId() == holder.view.getId()) {
                    ((OnPlaceSelectedListener)mContext).onPlaceSelected(position);
                } else if(isLongClick || view.getId() == holder.phone.getId()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+place.getPhone()));
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        return new ViewHolder(view);
    }

    public MessageResponse.Place removeItem(int position) {
        final MessageResponse.Place model = mPlaces.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, MessageResponse.Place model) {
        mPlaces.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final MessageResponse.Place model = mPlaces.remove(fromPosition);
        mPlaces.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<MessageResponse.Place> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<MessageResponse.Place> newModels) {
        for (int i = mPlaces.size() - 1; i >= 0; i--) {
            final MessageResponse.Place model = mPlaces.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<MessageResponse.Place> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final MessageResponse.Place model = newModels.get(i);
            if (!mPlaces.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MessageResponse.Place> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final MessageResponse.Place model = newModels.get(toPosition);
            final int fromPosition = mPlaces.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
