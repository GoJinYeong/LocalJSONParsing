package com.example.hyein.iotproject;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RecipeName {
    public String recipe_id;
    public String recipe_nm;
    public String sumry;
    public String cooking_time;
    public String calorie;
    public String level_nm;


    public RecipeName(String recipe_id, String recipe_nm, String sumry, String cooking_time, String calorie, String level_nm) {
        this.recipe_id = recipe_id;
        this.recipe_nm = recipe_nm;
        this.sumry = sumry;
        this.cooking_time = cooking_time;
        this.calorie = calorie;
        this.level_nm = level_nm;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public String getRecipe_nm() {
        return recipe_nm;
    }

    public String getSumry() {
        return sumry;
    }

    public String getCooking_time() {
        return cooking_time;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getLevel_nm() {
        return level_nm;
    }
}

