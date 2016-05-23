package com.icmc.ic.bixomaps.views.adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.fragments.OnPlaceSelectedListener;
import com.icmc.ic.bixomaps.models.MessageResponse;
import com.icmc.ic.bixomaps.views.ItemClickListener;

import java.util.List;

/**
 * Place list Adapter
 * Created by caiolopes on 5/15/16.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    public static final String TAG = PlaceAdapter.class.getSimpleName();
    Context mContext;
    List<MessageResponse.Place> mPlaces;
    Location mLocation;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView distance;
        ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.place_name);
            distance = (TextView) itemView.findViewById(R.id.place_distance);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getLayoutPosition(), false);
        }
    }

    public PlaceAdapter(Context context, List<MessageResponse.Place> places, Location location) {
        mContext = context;
        mPlaces = places;
        mLocation = location;
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    @Override
    public void onBindViewHolder(final PlaceAdapter.ViewHolder holder, int position) {
        MessageResponse.Place place = mPlaces.get(position);
        Location location = new Location("Place Location");
        location.setLatitude(Double.parseDouble(place.getLat()));
        location.setLongitude(Double.parseDouble(place.getLong()));
        holder.name.setText(place.getName());
        holder.distance.setText(mContext.getString(R.string.distance_km, mLocation.distanceTo(location)/1000));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (mContext instanceof OnPlaceSelectedListener) {
                    ((OnPlaceSelectedListener)mContext).onPlaceSelected(position);
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
}
