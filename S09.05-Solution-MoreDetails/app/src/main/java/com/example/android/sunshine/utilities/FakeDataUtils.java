package com.example.android.sunshine.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.FoodContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.android.sunshine.data.FoodContract.FoodEntry;

public class FakeDataUtils {

    private static int [] weatherIDs = {200,300,500,711,900,962};
    private static String[] testNames = {
            "apples",
            "chicken",
            "eggs",
            "pesto",
            "tomatoes",
            "spinach"};

    /**
     * Creates a single ContentValues object with random weather data for the provided date
     * @param date a normalized date
     * @return ContentValues object filled with random weather data
     */
    private static ContentValues createTestWeatherContentValues(long date, String name) {
        ContentValues testWeatherValues = new ContentValues();
        testWeatherValues.put(FoodContract.FoodEntry.COLUMN_DATE, date);
//        testWeatherValues.put(FoodEntry.COLUMN_NAME, weatherIDs[(int)(Math.random()*10)%5]);
        testWeatherValues.put(FoodEntry.COLUMN_NAME, name);
        return testWeatherValues;
    }

    /**
     * Creates random weather data for 7 days starting today
     * @param context
     */
    public static void insertFakeData(Context context) {
        //Get today's normalized date
        long today = CustomDateUtils.normalizeDate(System.currentTimeMillis());
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        //loop over 7 days starting today onwards
        for(int i=0; i<6; i++) {
            fakeValues.add(
                    FakeDataUtils.createTestWeatherContentValues(
                            today - TimeUnit.DAYS.toMillis(i), testNames[i]));
        }
        // Bulk Insert our new weather data into Sunshine's Database
        context.getContentResolver().bulkInsert(
                FoodEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[6]));
    }

    public static String fakeRecipeSearchResults() {
        return "{\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 262682,\n" +
                "      \"title\": \"Thai Sweet Potato Veggie Burgers with Spicy Peanut Sauce\",\n" +
                "      \"readyInMinutes\": 75,\n" +
                "      \"image\": \"thai-sweet-potato-veggie-burgers-with-spicy-peanut-sauce-262682.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"thai-sweet-potato-veggie-burgers-with-spicy-peanut-sauce-262682.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 227961,\n" +
                "      \"title\": \"Cajun Spiced Black Bean and Sweet Potato Burgers\",\n" +
                "      \"readyInMinutes\": 20,\n" +
                "      \"image\": \"Cajun-Spiced-Black-Bean-and-Sweet-Potato-Burgers-227961.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"Cajun-Spiced-Black-Bean-and-Sweet-Potato-Burgers-227961.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 602708,\n" +
                "      \"title\": \"Meatless Monday: Grilled Portobello Mushroom Burgers with Romesco and Arugula\",\n" +
                "      \"readyInMinutes\": 15,\n" +
                "      \"image\": \"Meatless-Monday--Grilled-Portobello-Mushroom-Burgers-with-Romesco-and-Arugula-602708.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"Meatless-Monday--Grilled-Portobello-Mushroom-Burgers-with-Romesco-and-Arugula-602708.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 759739,\n" +
                "      \"title\": \"Gluten-Free Veggie Burger\",\n" +
                "      \"readyInMinutes\": 45,\n" +
                "      \"image\": \"gluten-free-veggie-burger-759739.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"gluten-free-veggie-burger-759739.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 630255,\n" +
                "      \"title\": \"Protein Powerhouse Veggie Burgers\",\n" +
                "      \"readyInMinutes\": 95,\n" +
                "      \"image\": \"Protein-Powerhouse-Veggie-Burgers-630255.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"Protein-Powerhouse-Veggie-Burgers-630255.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 479732,\n" +
                "      \"title\": \"Meatless Monday: Curried Veggie Burgers with Zucchini, Lentils, and Quinoa\",\n" +
                "      \"readyInMinutes\": 15,\n" +
                "      \"image\": \"Meatless-Monday--Curried-Veggie-Burgers-with-Zucchini--Lentils--and-Quinoa-479732.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"Meatless-Monday--Curried-Veggie-Burgers-with-Zucchini--Lentils--and-Quinoa-479732.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 541691,\n" +
                "      \"title\": \"Black Bean Mole Burgers\",\n" +
                "      \"readyInMinutes\": 45,\n" +
                "      \"image\": \"black-bean-mole-burgers-541691.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"black-bean-mole-burgers-541691.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 34035,\n" +
                "      \"title\": \"Sprouted Lentil Veggie Burger\",\n" +
                "      \"readyInMinutes\": 30,\n" +
                "      \"image\": \"sprouted_lentil_veggie_burger-34035.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"sprouted_lentil_veggie_burger-34035.jpg\",\n" +
                "        \"sprouted-lentil-veggie-burger-2-34035.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 766301,\n" +
                "      \"title\": \"Queso Cheese Burgers\",\n" +
                "      \"readyInMinutes\": 60,\n" +
                "      \"image\": \"queso-cheese-burgers-766301.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"queso-cheese-burgers-766301.jpg\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 761774,\n" +
                "      \"title\": \"Simple Soybean Burgers\",\n" +
                "      \"readyInMinutes\": 45,\n" +
                "      \"image\": \"simple-soybean-burgers-761774.jpg\",\n" +
                "      \"imageUrls\": [\n" +
                "        \"simple-soybean-burgers-761774.jpg\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"baseUri\": \"https://spoonacular.com/recipeImages/\",\n" +
                "  \"offset\": 0,\n" +
                "  \"number\": 10,\n" +
                "  \"totalResults\": 10,\n" +
                "  \"processingTimeMs\": 323,\n" +
                "  \"expires\": 1473587241426,\n" +
                "  \"isStale\": false\n" +
                "}";
    }
}
