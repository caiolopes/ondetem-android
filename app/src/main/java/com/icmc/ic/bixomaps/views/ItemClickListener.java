package com.icmc.ic.bixomaps.views;

import android.view.View;

/**
 * Interface for RecyclerView adapters to be clickable
 * @author caiolopes
 */
public interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
