package com.icmc.ic.bixomaps;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.icmc.ic.bixomaps.models.Category;
import com.icmc.ic.bixomaps.views.adapters.CategoryAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Categories List Activity.
 *
 * @author caiolopes
 */
public class MainActivity extends AppBaseActivity implements SearchView.OnQueryTextListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CategoryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Category> mAllItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createCategoriesMenu();
        setupSearch();
    }

    private void setupSearch() {

    }

    private Category createCategory(int string, String tag, int icon) {
        return new Category(getString(string), tag, icon);
    }

    /**
     * Creating categories menu menu
     */
    void createCategoriesMenu() {
        List<Category> mCategories = new ArrayList<>();
        mCategories.add(createCategory(R.string.category_car, "car_services", R.drawable.ic_action_car));
        mCategories.add(createCategory(R.string.category_bank, "bank", R.drawable.ic_action_creditcard));
        mCategories.add(createCategory(R.string.category_education, "education", R.drawable.ic_action_book));
        mCategories.add(createCategory(R.string.category_medical, "medical_care", R.drawable.ic_hospital));
        mCategories.add(createCategory(R.string.category_emergency, "emergency_services", R.drawable.ic_emergency));
        mCategories.add(createCategory(R.string.category_culture, "culture_entertainment", R.drawable.ic_action_music_1));
        mCategories.add(createCategory(R.string.category_food, "food_drink", R.drawable.ic_action_restaurant));
        mCategories.add(createCategory(R.string.category_government, "government", R.drawable.ic_account_balance));
        mCategories.add(createCategory(R.string.category_lodging, "lodging", R.drawable.ic_hotel));
        mCategories.add(createCategory(R.string.category_recreation, "recreation", R.drawable.ic_action_bike));
        mCategories.add(createCategory(R.string.category_public_services, "public_services", R.drawable.ic_action_business));
        mCategories.add(createCategory(R.string.category_shops, "service_shops_stores", R.drawable.ic_action_cart));
        mCategories.add(createCategory(R.string.category_transport, "public_transport", R.drawable.ic_action_bus));
        mCategories.add(createCategory(R.string.category_worship, "places_worship", R.drawable.ic_church));
        mCategories.add(createCategory(R.string.category_home, "basic_home_services", R.drawable.ic_action_home));
        mCategories.add(createCategory(R.string.category_beauty, "beauty_care", R.drawable.ic_beauty));
        mCategories.add(createCategory(R.string.category_administrative, "administrative_services", R.drawable.ic_action_stamp));
        mCategories.add(createCategory(R.string.category_animal, "animal_care", R.drawable.ic_pets));

        Collections.sort(mCategories, new Comparator<Category>() {
            @Override
            public int compare(Category category, Category t1) {
                return category.getName().compareTo(t1.getName());
            }
        });

        mAllItems = new ArrayList<>();
        mAllItems.addAll(mCategories);

        mRecyclerView = (RecyclerView) findViewById(R.id.menu_list);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CategoryAdapter(this, mCategories);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Configure the search info and add any event listeners...
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        final List<Category> filteredModelList = filter(mAllItems, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Category> filter(List<Category> models, String query) {
        query = query.toLowerCase();

        final List<Category> filteredModelList = new ArrayList<>();
        for (Category model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }

        return filteredModelList;
    }

    @Override
    protected void locationUpdate(Location location) {
    }
}
