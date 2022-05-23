package com.example.hyein.iotproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    ArrayAdapter adapter;
    TextView textView;
    Button button;
    String key2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        getSupportActionBar().setTitle("재료체크");

        textView = (TextView) findViewById(R.id.textView3);

        final Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            key2 = bundle.getString("key2");
            textView.setText( key2 + " 의 재료체크 입니다.");
        }

        ArrayList<String> igntName = findIgntName(key2);

        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
                intent.putExtra("key3",bundle.getString("key2"));
                startActivity(intent);
            }
        });

        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, igntName);

        listView = (ListView) findViewById(R.id.listView3);
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

    public ArrayList<Ingredient> parsingIGNT(){
        int json_id =  R.raw.recipe_ignt;
        String read = loadJSON(json_id);
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        String recipe_id;
        String irdnt_nm;
        String irdnt_cpcty;
        int JSONArrayCount;

        try {
            JSONObject jsonObject = new JSONObject(read);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            JSONArrayCount = jsonArray.length();

            for (int i = 0; i < JSONArrayCount; i++) {
                JSONObject in_jsonObject = jsonArray.getJSONObject(i);
                recipe_id = in_jsonObject.getString("RECIPE_ID");
                irdnt_nm = in_jsonObject.getString("IRDNT_NM");
                irdnt_cpcty = in_jsonObject.getString("IRDNT_CPCTY");

                Ingredient ingredient = new Ingredient(recipe_id, irdnt_nm, irdnt_cpcty);

                ingredients.add(ingredient);
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

        String recipe_id ;
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

            for (int i = 0; i < JSONArrayCount; i++) {
                JSONObject in_jsonObject = jsonArray.getJSONObject(i);
                recipe_id = in_jsonObject.getString("RECIPE_ID");
                recipe_nm = in_jsonObject.getString("RECIPE_NM_KO");
                sumry = in_jsonObject.getString("SUMRY");
                cooking_time = in_jsonObject.getString("COOKING_TIME");
                calorie = in_jsonObject.getString("CALORIE");
                level_nm = in_jsonObject.getString("LEVEL_NM");

                RecipeName recipeName = new RecipeName(recipe_id, recipe_nm, sumry, cooking_time, calorie, level_nm);

                recipeNames.add(recipeName);
            }

        } catch (JSONException je){
            je.printStackTrace();
        }
        return recipeNames;
    }

    public ArrayList<String> findIgntName(String igntName){
        String recipe_id = "";
        String recipeNM;
        String ignt_recipeID;
        String select_igntName;
        String irdnt_cpcty;
        String ignt;
        ArrayList<Ingredient> ingredients = parsingIGNT();
        ArrayList<RecipeName> recipeNames = parsingName();

        int recipeNameCount = recipeNames.size();
        int igntCount = ingredients.size();

        for (int i = 0; i < recipeNameCount; i++){
            recipeNM = recipeNames.get(i).getRecipe_nm();
            if (recipeNM.equals(igntName)){
                recipe_id = recipeNames.get(i).getRecipe_id();
                break;
            }
        }

        ArrayList<String> select_ignt = new ArrayList<>();

        for (int i = 0; i < igntCount; i++){
            ignt_recipeID = ingredients.get(i).getRecipe_id();
            if ( ignt_recipeID.equals(recipe_id)){
                irdnt_cpcty = ingredients.get(i).getIrdnt_cpcty();
                select_igntName = ingredients.get(i).getIrdnt_nm();
                ignt = select_igntName + " " + irdnt_cpcty;
                select_ignt.add(ignt);
            }
        }
        return select_ignt;
    }
}
