package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RecipesActivity extends AppCompatActivity {

    private static final String URL_BASE =
//            "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/search?offset=0&query=";
            "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false&ingredients=";

    // refactor into recyclerView list item
    private TextView mResultsTextView;
    private TextView mRecipeName;
    private TextView mReadyMinutes;
    private ImageView mPic;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private JSONArray mRecipeResults;

    private String[] mFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        mRecipeName = (TextView) findViewById(R.id.tv_recipe_name);
        mReadyMinutes = (TextView) findViewById(R.id.tv_used_ingredients_count);
        mPic = (ImageView) findViewById(R.id.iv_recipe_pic);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipes);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFoods = getIntent().getExtras().getStringArray("foods");

        new SpoonacularSearchTask().execute(makeSearchQuery());
    }

    private URL makeSearchQuery() {

        String urlString = URL_BASE;

        if (mFoods.length >= 1) {
            urlString += mFoods[0];
        }

//        int numItemsToQuery = mFoods.length;
//        if (mFoods.length > 3) {
//            numItemsToQuery = 3;
//        }

        for (int i = 1; i < mFoods.length; i++) {
            urlString += "%2C" +  mFoods[i]; // vs "+"
        }
        urlString += "&ranking=2"; // add option for 1=maximize used or 2=minimize missing

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public class SpoonacularSearchTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

//            return FakeDataUtils.fakeRecipeSearchResults();
            StringBuilder response = null;

            try {

                URLConnection connection = urls[0].openConnection();
                connection.setRequestProperty("X-Mashape-Key", "TyzoL5wDIGmshTDH7Jccy4e88NJEp15YcuYjsnFbzup4sC4INc");
                connection.setRequestProperty("Accept", "application/json");

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String recipeResult) {

            JSONArray results = null;

            if (recipeResult != null && recipeResult != "") {

                try {
//                    JSONObject jsonObject = new JSONObject(recipeResult);
//                    results = jsonObject.getJSONArray("results");
                    results = new JSONArray(recipeResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mAdapter = new RecipeAdapter(results);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
