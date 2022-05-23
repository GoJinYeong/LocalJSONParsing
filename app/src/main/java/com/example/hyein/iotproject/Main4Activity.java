package com.example.hyein.iotproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Main4Activity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    ArrayAdapter adapter;
    String key3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        getSupportActionBar().setTitle("레시피");

        final Bundle bundle = getIntent().getExtras();
        key3 = bundle.getString("key3");

        ArrayList<String> recipe = findRecipe(key3);

        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, recipe);

        listView = (ListView) findViewById(R.id.listView4);
        listView.setAdapter(adapter);
    }

    public String loadJSON(int path){
        String json = null;

        try{
            InputStream in = getResources().openRawResource(path);
            if (in != null){
                int size = in.available();

                byte[] buffer = new byte[size];

                in.read(buffer);
                in.close();

                json = new String(buffer, "UTF-8");
            }
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<RecipeCooking> parsingProcess(){
        int json_id =  R.raw.recipe_cooking;
        String read = loadJSON(json_id);
        ArrayList<RecipeCooking> recipeCookings = new ArrayList<RecipeCooking>();

        String recipe_id;
        String cooking_no;
        String cooking_dc;
        String stre_step_img_url;
        String step_tip;
        int JSONArrayCount;

        try {
            JSONObject jsonObject = new JSONObject(read);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONArrayCount = jsonArray.length();

            for (int i = 0; i < JSONArrayCount; i++) {
                JSONObject in_jsonObject = jsonArray.getJSONObject(i);
                recipe_id = in_jsonObject.getString("RECIPE_ID");
                cooking_no = in_jsonObject.getString("COOKING_NO");
                cooking_dc = in_jsonObject.getString("COOKING_DC");
                stre_step_img_url = in_jsonObject.getString("STRE_STEP_IMAGE_URL");
                step_tip = in_jsonObject.getString("STEP_TIP");

                RecipeCooking recipeCooking = new RecipeCooking(recipe_id, cooking_no, cooking_dc, stre_step_img_url, step_tip);

                recipeCookings.add(recipeCooking);
            }

        } catch (JSONException je){
            je.printStackTrace();
        }

        return recipeCookings;

    }

    public ArrayList<RecipeName> parsingName(){
        int json_id =  R.raw.recipe_name;
        String read = loadJSON(json_id);
        ArrayList<RecipeName> recipeNames = new ArrayList<>();

        String recipe_id;
        String recipe_nm;
        String sumry;
        String cooking_time;
        String calorie;
        String level_nm;
        int JSONArrayCount;

        try {
            JSONObject jsonObject = new JSONObject(read);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONArrayCount = jsonArray.length();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject in_jsonObject = jsonArray.getJSONObject(i);
                recipe_id = in_jsonObject.getString("RECIPE_ID");
                recipe_nm = in_jsonObject.getString("RECIPE_NM_KO");
                sumry = in_jsonObject.getString("SUMRY");
                cooking_time = in_jsonObject.getString("COOKING_TIME");
                calorie = in_jsonObject.getString("CALORIE");
                level_nm = in_jsonObject.getString("LEVEL_NM");

                RecipeName recipeName = new RecipeName(recipe_id, recipe_nm, sumry, cooking_time, calorie, level_nm);

                recipeNames.add(i, recipeName);
            }

        } catch (JSONException je){
            je.printStackTrace();
        }

        return recipeNames;

    }

    public ArrayList<String> findRecipe(String recipeName){
        String recipe_id="";
        String cooking_no;
        String cooking_dc;
        String stre_step_img_url;
        String step_tip;

        ArrayList<RecipeCooking> recipeCookings = parsingProcess();
        ArrayList<RecipeName> recipeNames = parsingName();

        int recipeNameCount = recipeNames.size();
        String get_recipeName;

        for (int i = 0; i < recipeNameCount; i++){
            get_recipeName = recipeNames.get(i).getRecipe_nm();
            if (get_recipeName.equals(recipeName)){
                recipe_id = recipeNames.get(i).getRecipe_id();
                break;
            }
        }

        ArrayList<String> process = new ArrayList<>();
        int cookingCount = recipeCookings.size();
        String get_cooking;

        for (int i = 0; i < cookingCount; i++){
            get_cooking = recipeCookings.get(i).getRecipe_id();
            if (get_cooking.equals(recipe_id)){
                cooking_no = recipeCookings.get(i).getCooking_no();
                cooking_dc = recipeCookings.get(i).getCooking_dc();
                stre_step_img_url = recipeCookings.get(i).getStre_step_img_url();
                step_tip = recipeCookings.get(i).getStep_tip();

                String no_dc = cooking_no + ". " + cooking_dc;

                process.add(no_dc);
            }
        }
        return process;
    }
}
