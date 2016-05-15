package com.icmc.ic.bixomaps;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.icmc.ic.bixomaps.views.adapters.MenuAdapter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Categories List Activity.
 *
 * @author caiolopes
 */
public class MainActivity extends AppBaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public MenuAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createCategoriesMenu();
    }

    /**
     * Creating categories menu menu
     */
    void createCategoriesMenu() {
        Map<String, Integer> mCategoryMenu = new TreeMap<>();
        mCategoryMenu.put(getString(R.string.category_car), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_bank), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_education), R.drawable.ic_menu_education_vector);
        mCategoryMenu.put(getString(R.string.category_medical), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_emergency), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_culture), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_food), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_government), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_lodging), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_recreation), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_publicservices), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_shops), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_transport), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_worship), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_home), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_beauty), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_administrative), R.drawable.ic_menu_restaurant_vector);
        mCategoryMenu.put(getString(R.string.category_animal), R.drawable.ic_menu_restaurant_vector);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menu_list);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        mAdapter = new MenuAdapter(this, mCategoryMenu);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void locationUpdate(Location location) {
        Log.v(TAG, "Location Update!");
    }
}
