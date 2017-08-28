/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.android.sunshine.data.FoodContract;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.CustomDateUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FoodAdapter.FoodAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] MAIN_FOOD_PROJECTION = {
            FoodContract.FoodEntry.COLUMN_DATE,
            FoodContract.FoodEntry.COLUMN_NAME,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_DATE = 0;
    public static final int INDEX_NAME = 1;

    /*
     * This ID will be used to identify the Loader responsible for loading our weather forecast. In
     * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
     * We will still use this ID to initialize the loader and create the loader for best practice.
     * Please note that 44 was chosen arbitrarily. You can use whatever number you like, so long as
     * it is unique and consistent.
     */
    private static final int ID_FORECAST_LOADER = 44;

    private RecyclerView mRecyclerView;
    private FoodAdapter mFoodAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    private FloatingActionButton mFab;
    private Button mRecipes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);

//        FakeDataUtils.insertFakeData(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecipes = (Button) findViewById(R.id.fab_recipes);
        mRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent recipeIntent = new Intent(MainActivity.this, RecipesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("foods", getItemsAsArray());
                recipeIntent.putExtras(bundle);

                startActivity(recipeIntent);
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab_add);
        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Add Item");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String nameToAdd = input.getText().toString();
                                long date = CustomDateUtils.normalizeDate(System.currentTimeMillis());

                                ContentValues foodToAdd = new ContentValues();
                                foodToAdd.put(FoodContract.FoodEntry.COLUMN_NAME, nameToAdd);
                                foodToAdd.put(FoodContract.FoodEntry.COLUMN_DATE, date);

                                ContentValues[] values = {foodToAdd};

                                getContentResolver().bulkInsert(
                                        FoodContract.FoodEntry.CONTENT_URI,
                                        values);
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
////                startActivity(new Intent(MainActivity.this, AddActivity.class));
//
//                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
//                        .getSystemService(LAYOUT_INFLATER_SERVICE);
//                View customView = inflater.inflate(R.layout.activity_add, null);
//
//                FrameLayout mainLayout = (FrameLayout) findViewById(R.id.main_layout);
//                double height = mainLayout.getMeasuredHeight() * 0.3;
//                double width =  mainLayout.getMeasuredWidth() * 0.8;
//
//                mAddPopup = new PopupWindow(
//                        customView,
//                        (int) width,
//                        (int) height
//                );
//
//                if(Build.VERSION.SDK_INT >= 21){
//                    mAddPopup.setElevation(5.0f);
//                }
//
//                final View popupView = mAddPopup.getContentView();
//                Button cancelBut = (Button) popupView.findViewById(R.id.but_cancel);
//                cancelBut.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // Dismiss the popup window
//                        mAddPopup.dismiss();
//                    }
//                });
//
//                Button addBut = (Button) popupView.findViewById(R.id.but_ok);
//                addBut.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        EditText addName = (EditText) popupView.findViewById(R.id.et_food_name);
//                        String nameToAdd = addName.getText().toString();
//                        long date = CustomDateUtils.normalizeDate(System.currentTimeMillis());
//
//                        ContentValues foodToAdd = new ContentValues();
//                        foodToAdd.put(FoodContract.FoodEntry.COLUMN_NAME, nameToAdd);
//                        foodToAdd.put(FoodContract.FoodEntry.COLUMN_DATE, date);
//
//                        ContentValues[] values = {foodToAdd};
//
//                        getContentResolver().bulkInsert(
//                                FoodContract.FoodEntry.CONTENT_URI,
//                                values);
//
//                        mAddPopup.dismiss();
//                    }
//                });
//
//                mAddPopup.setFocusable(true);
//                mAddPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                mAddPopup.update();
//                mAddPopup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                String[] args = { viewHolder.toString() };
                getContentResolver().delete(FoodContract.FoodEntry.CONTENT_URI,
                        FoodContract.FoodEntry.COLUMN_NAME + "=?", args);
            }
        }).attachToRecyclerView(mRecyclerView);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mFoodAdapter = new FoodAdapter(this, this);
        mRecyclerView.setAdapter(mFoodAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
    }

    /**
     * Uses the URI scheme for showing a location found on a map in conjunction with
     * an implicit Intent. This super-handy Intent is detailed in the "Common Intents" page of
     * Android's developer site:
     *
     * @see "http://developer.android.com/guide/components/intents-common.html#Maps"
     * <p>
     * Protip: Hold Command on Mac or Control on Windows and click that link to automagically
     * open the Common Intents page
     */
    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    /**
     * Called by the {@link android.support.v4.app.LoaderManagerImpl} when a new Loader needs to be
     * created. This Activity only uses one loader, so we don't necessarily NEED to check the
     * loaderId, but this is certainly best practice.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_FORECAST_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri foodQueryUri = FoodContract.FoodEntry.CONTENT_URI;
                String sortOrder = FoodContract.FoodEntry.COLUMN_DATE + " ASC";
                String selection = null;

                return new CursorLoader(this,
                        foodQueryUri,
                        MAIN_FOOD_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
     * initial load being performed with no access to internet, the loading indicator will show
     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
     * future version of the course.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mFoodAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        mRecyclerView.smoothScrollToPosition(mPosition);
        showFoodDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mFoodAdapter.swapCursor(null);
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param date Normalized UTC time that represents the local date of the weather in GMT time.
     * @see FoodContract.FoodEntry#COLUMN_DATE
     */
    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = FoodContract.FoodEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        try {
            startActivity(weatherDetailIntent);
        } catch (Exception e) {
            Log.e("Execption output: ", e.toString());
        }
    }

    private void showFoodDataView() {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {

        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private Cursor getAllItems() {

        return getContentResolver().query(
                FoodContract.FoodEntry.CONTENT_URI,
//                new String[] { FoodContract.FoodEntry.COLUMN_NAME },
                null,
                null,
                null,
                null
        );
    }

    private String[] getItemsAsArray() {

        Cursor cursor = getAllItems();
        ArrayList<String> itemsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            itemsList.add(cursor.getString(2)); // WTFFFFF WHY IS THIS NOT INDEX_NAME
        }

        return itemsList.toArray(new String[itemsList.size()]);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
