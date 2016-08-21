package com.icmc.ic.bixomaps.views.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icmc.ic.bixomaps.MainActivity;
import com.icmc.ic.bixomaps.PlaceActivity;
import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.models.Category;
import com.icmc.ic.bixomaps.views.ItemClickListener;

import java.util.List;

/**
 * RecyclerView adapter for listing categories for the user
 * @author caiolopes
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = CategoryAdapter.class.getSimpleName();
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private List<Category> mCategories;
    private String focusedItem = "";
    private MainActivity mContext;

    public static class MenuItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout box;
        TextView text;
        ProgressBar progress;
        ItemClickListener itemClickListener;
        public MenuItem(View itemView) {
            super(itemView);
            box = (LinearLayout) itemView.findViewById(R.id.category_box);
            text = (TextView) itemView.findViewById(android.R.id.text1);
            progress = (ProgressBar) itemView.findViewById(R.id.category_loading);
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

    public static class Header extends RecyclerView.ViewHolder {
        public Header(View itemView) {
            super(itemView);
        }
    }

    public CategoryAdapter(MainActivity mainActivity, List<Category> categories) {
        mContext = mainActivity;
        mCategories = categories;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MenuItem) {
            final MenuItem item = (MenuItem) holder;
            final Category category = mCategories.get(position-1);
            item.progress.setVisibility(View.GONE);
            if (category.getName().compareTo(focusedItem) == 0) {
                item.box.setBackgroundColor(mContext.getResources().getColor(R.color.mediumGray));
                item.text.setTextColor(Color.BLUE);
            } else {
                item.box.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light));
                item.text.setTextColor(Color.BLACK);
            }
            item.text.setText(category.getName());
            item.text.setCompoundDrawablesWithIntrinsicBounds(category.getIcon(), 0, 0, 0);
            item.text.setCompoundDrawablePadding(15);
            item.text.setClickable(true);
            item.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    int pos = 0;
                    for (Category c : mCategories) {
                        if (c.getName().compareTo(focusedItem) == 0) break;
                        pos++;
                    }
                    focusedItem = category.getName();
                    notifyItemChanged(pos+1);
                    notifyItemChanged(position);
                    item.progress.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(mContext, PlaceActivity.class);
                    intent.putExtra("CATEGORY", category.getTag());
                    intent.putExtra("TITLE", item.text.getText().toString());
                    mContext.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position > 0 ? ITEM : HEADER;
    }

    @Override
    public int getItemCount() {
        return mCategories.size()+1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nav_header_main, parent, false);
            return new Header(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_list_item, parent, false);
            return new MenuItem(view);
        }
    }

    public Category removeItem(int position) {
        final Category model = mCategories.remove(position);
        notifyItemRemoved(position+1);
        return model;
    }

    public void addItem(int position, Category model) {
        mCategories.add(position, model);
        notifyItemInserted(position+1);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Category model = mCategories.remove(fromPosition);
        mCategories.add(toPosition, model);
        notifyItemMoved(fromPosition+1, toPosition+1);
    }

    public void animateTo(List<Category> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Category> newModels) {
        for (int i = mCategories.size() - 1; i >= 0; i--) {
            final Category model = mCategories.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Category> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Category model = newModels.get(i);
            if (!mCategories.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Category> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Category model = newModels.get(toPosition);
            final int fromPosition = mCategories.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
