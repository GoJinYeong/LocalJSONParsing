package com.example.hyein.iotproject;


import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.policy.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    ArrayAdapter adapter;
    TextView textView;
    String key1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportActionBar().setTitle("요리목록");

        textView = (TextView) findViewById(R.id.ingredient);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            key1 = bundle.getString("key1");
            textView.setText( key1 + " 을/를 주재료로 한 요리입니다.");
        }

        ArrayList<String> cookNames = findCookName(key1);

        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, cookNames);

        listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
                intent.putExtra("key2",listView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
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

    public ArrayList<Ingredient> parsingIGNT(){
        int json_id =  R.raw.recipe_ignt;
        String read = loadJSON(json_id);
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        String recipe_id = null;
        String irdnt_nm = null;
        int jsonSize;

        try {
            JSONObject jsonObject = new JSONObject(read);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            jsonSize = jsonArray.length();

            for (int i = 0; i < jsonSize; i++) {
                JSONObject in_jsonObject = jsonArray.getJSONObject(i);
                recipe_id = in_jsonObject.getString("RECIPE_ID");
                irdnt_nm = in_jsonObject.getString("IRDNT_NM");

                Ingredient ingredient = new Ingredient(recipe_id, irdnt_nm);

                ingredients.add(i, ingredient);
            }

        } catch (JSONException je){
            je.printStackTrace();
        }
        return ingredients;
    }

    public ArrayList<RecipeName> parsingName(){
        int json_id =  R.raw.recipe_name;
        String read = loadJSON(json_id);
        ArrayList<RecipeName> recipeNames = new ArrayList<>();

        String recipe_id = null;
        String recipe_nm = null;
        String sumry = null;
        String cooking_time = null;
        String calorie = null;
        String level_nm = null;
        int jsonSize;

        try {
            JSONObject jsonObject = new JSONObject(read);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            jsonSize = jsonArray.length();

            for (int i = 0; i < jsonSize; i++) {
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

    public ArrayList<Ingredient> findIgntName(String igntName){
        String JSON_igntName;
        ArrayList<Ingredient> ingredients = parsingIGNT();
        ArrayList<Ingredient> select = new ArrayList<>();

        int igntCount = ingredients.size();

        for (int i = 0; i < igntCount; i++){
            JSON_igntName = ingredients.get(i).getIrdnt_nm();
            if (JSON_igntName.equals(igntName)){
               select.add(ingredients.get(i));
            }
        }
        return select;
    }

    public ArrayList<String> findCookName(String igntName){
        String cookName;
        String recipeID;
        ArrayList<String> cookNames = new ArrayList<>();
        ArrayList<RecipeName> recipeNames = parsingName();

        ArrayList<Ingredient> select = findIgntName(igntName);
        int recipeNameCount = recipeNames.size();
        int selectCount = select.size();

        for (int j = 0; j < recipeNameCount; j++){
            recipeID = recipeNames.get(j).getRecipe_id();
            for (int a = 0; a < selectCount; a++){
                if (recipeID.equals(select.get(a).getRecipe_id())) {
                    cookName = recipeNames.get(j).getRecipe_nm();
                    cookNames.add(cookName);
                }
            }
        }
        return cookNames;
    }
}
