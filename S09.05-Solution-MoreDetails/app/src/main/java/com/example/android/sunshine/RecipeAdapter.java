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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private JSONArray mRecipeResults;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mRecipeName;
        private TextView mReadyMinutes;
        private ImageView mPic;

        public ViewHolder (View view) {

            super(view);

            mRecipeName = (TextView) view.findViewById(R.id.tv_recipe_name);
            mReadyMinutes = (TextView) view.findViewById(R.id.tv_used_ingredients_count);
            mPic = (ImageView) view.findViewById(R.id.iv_recipe_pic);
        }
    }

    public RecipeAdapter(JSONArray recipeResults) {

        this.mRecipeResults = recipeResults;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);

        view.setFocusable(true);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        String name = null;
        String usedCount = null;
        String missedCount = null;
        String imageUrl = "https://spoonacular.com/recipeImages/";

        try {
            JSONObject recipe = mRecipeResults.getJSONObject(position);

            name = recipe.getString("title");

//            minutes = recipe.getString("readyInMinutes");
//            imageUrl = imageUrl + recipe.getJSONArray("imageUrls").getString(0);
            imageUrl = recipe.getString("image");

        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.mRecipeName.setText(name);

        new ImageLoadTask(imageUrl, viewHolder.mPic).execute();
    }

    @Override
    public int getItemCount() {
        return mRecipeResults.length();
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String imageUrl;
        private ImageView imageView;

        public ImageLoadTask(String imageUrl, ImageView imageView) {
            this.imageUrl = imageUrl;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL urlConnection = new URL(imageUrl);
                URLConnection connection = urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream is = connection.getInputStream();
                return BitmapFactory.decodeStream(is);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);

            if (result != null) {
                double scale = 1;
                int width = (int) (result.getWidth() / scale);
                int height = (int) (result.getHeight() / scale);

                Bitmap cropped;

                // crops image into a square for formatting
                if (width <= height) {

                    int x = 0;
                    int y = (height - width) / 2;
                    cropped = Bitmap.createBitmap(result, x, y, width, width);
                } else {

                    int x = (width - height) / 2;
                    int y = 0;
                    cropped = Bitmap.createBitmap(result, x, y, height, height);
                }

                imageView.setImageBitmap(cropped);
            }
        }
    }
}