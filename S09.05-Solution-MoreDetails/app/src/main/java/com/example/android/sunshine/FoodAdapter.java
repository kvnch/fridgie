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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.ParallelExecutorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.data.FoodContract;
import com.example.android.sunshine.utilities.CustomDateUtils;

/**
 * {@link FoodAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}.
 */
class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodAdapterViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our FoodAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private FoodAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface FoodAdapterOnClickHandler {
        void onClick(long date);
    }

    private Cursor mCursor;

    /**
     * Creates a FoodAdapter.
     *
     * @param context Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public FoodAdapter(@NonNull Context context, FoodAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new FoodAdapterViewHolder that holds the View for each list item
     */
    @Override
    public FoodAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.forecast_list_item, viewGroup, false);

        view.setFocusable(true);

        return new FoodAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(FoodAdapterViewHolder viewHolder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        /* Give a tag to this item in the list */
//        long id = mCursor.getLong(mCursor.getColumnIndex(FoodContract.FoodEntry._ID));
//        viewHolder.itemView.setTag(id);

        /* Read date from the cursor */
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_DATE);
        int daysDifference =  CustomDateUtils.getDaysDifference(dateInMillis);

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.circle_shape);
        String daysText;

        if (daysDifference == 0) {
            drawable.setColorFilter(ContextCompat.getColor(mContext,R.color.colorL0), PorterDuff.Mode.SRC_ATOP);
            daysText = "Today";
        } else if (daysDifference == 1) {
            drawable.setColorFilter(ContextCompat.getColor(mContext,R.color.colorL1), PorterDuff.Mode.SRC_ATOP);
            daysText = daysDifference + " day";
        } else if (daysDifference >= 2 && daysDifference <= 5) {
            drawable.setColorFilter(ContextCompat.getColor(mContext,R.color.colorL2), PorterDuff.Mode.SRC_ATOP);
            daysText = daysDifference + " days";
        } else {
            drawable.setColorFilter(ContextCompat.getColor(mContext,R.color.colorL3), PorterDuff.Mode.SRC_ATOP);
            daysText = "A week";
        }

        /* Set Text Views in list */
        viewHolder.dayColor.setBackground(drawable);
        viewHolder.dayCount.setText(daysText);
        viewHolder.foodName.setText(mCursor.getString(MainActivity.INDEX_NAME));
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the FoodAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as FoodAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class FoodAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView foodName;
        final TextView dayColor;
        final TextView dayCount;

        FoodAdapterViewHolder(View view) {
            super(view);

            foodName = (TextView) view.findViewById(R.id.tv_weather_data);
            dayColor = (TextView) view.findViewById(R.id.tv_day_color);
            dayCount = (TextView) view.findViewById(R.id.tv_day_count);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_DATE);
            mClickHandler.onClick(dateInMillis);
        }

        @Override
        public String toString() {
            return foodName.getText().toString();
        }
    }
}