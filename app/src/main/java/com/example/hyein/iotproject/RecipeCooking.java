package com.example.hyein.iotproject;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RecipeCooking{
    public String recipe_id;
    public String cooking_no;
    public String cooking_dc;
    public String stre_step_img_url;
    public String step_tip;

    public int path = R.raw.recipe_cooking;

    public RecipeCooking() {
    }

    public RecipeCooking(String recipe_id, String cooking_no, String cooking_dc, String stre_step_img_url, String step_tip) {
        this.recipe_id = recipe_id;
        this.cooking_no = cooking_no;
        this.cooking_dc = cooking_dc;
        this.stre_step_img_url = stre_step_img_url;
        this.step_tip = step_tip;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public String getCooking_no() {
        return cooking_no;
    }

    public String getCooking_dc() {
        return cooking_dc;
    }

    public String getStre_step_img_url() {
        return stre_step_img_url;
    }


    public String getStep_tip() {
        return step_tip;
    }

}