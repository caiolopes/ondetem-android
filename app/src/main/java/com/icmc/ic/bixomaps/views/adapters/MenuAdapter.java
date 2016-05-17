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

import com.icmc.ic.bixomaps.PlaceActivity;
import com.icmc.ic.bixomaps.MainActivity;
import com.icmc.ic.bixomaps.R;
import com.icmc.ic.bixomaps.utils.Helper;
import com.icmc.ic.bixomaps.views.ItemClickListener;

import java.util.Map;

/**
 * RecyclerView adapter for the drawer menu
 * @author caiolopes
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = MenuAdapter.class.getSimpleName();
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private Map<String, Integer> mMenuList;
    private int focusedItem = 0;
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

    public MenuAdapter(MainActivity mainActivity, Map<String, Integer> menuList) {
        mContext = mainActivity;
        mMenuList = menuList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MenuItem) {
            final MenuItem item = (MenuItem) holder;

            item.progress.setVisibility(View.GONE);

            if (focusedItem == position) {
                item.box.setBackgroundColor(mContext.getResources().getColor(R.color.mediumGray));
                item.text.setTextColor(Color.BLUE);
            } else {
                item.box.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light));
                item.text.setTextColor(Color.BLACK);
            }

            int i = 0;
            for(Map.Entry<String,Integer> entry : mMenuList.entrySet()) {
                if (i == position-1) {
                    item.text.setText(entry.getKey());
                    item.text.setCompoundDrawablesWithIntrinsicBounds(entry.getValue(), 0, 0, 0);
                    item.text.setCompoundDrawablePadding(15);
                    item.text.setClickable(true);
                    item.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            int aux = focusedItem;
                            focusedItem = position;
                            notifyItemChanged(aux);
                            notifyItemChanged(focusedItem);
                            item.progress.setVisibility(View.VISIBLE);

                            String category = Helper.getCategoryField(item.text.getText().toString(), mContext);
                            Intent intent = new Intent(mContext, PlaceActivity.class);
                            intent.putExtra("CATEGORY", category);
                            intent.putExtra("TITLE", item.text.getText().toString());
                            mContext.startActivity(intent);

                        }
                    });

                    break;
                }
                i++;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position > 0 ? ITEM : HEADER;
    }

    @Override
    public int getItemCount() {
        return mMenuList.size()+1;
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
}
