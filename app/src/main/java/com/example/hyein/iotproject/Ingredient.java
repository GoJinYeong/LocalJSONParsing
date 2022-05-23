package com.example.hyein.iotproject;

public class Ingredient{
    public String recipe_id;
    public String irdnt_nm;
    public String irdnt_cpcty;

    public Ingredient() {
    }

    public Ingredient(String recipe_id, String irdnt_nm) {
        this.recipe_id = recipe_id;
        this.irdnt_nm = irdnt_nm;
    }

    public Ingredient(String recipe_id, String irdnt_nm, String irdnt_cpcty) {
        this.recipe_id = recipe_id;
        this.irdnt_nm = irdnt_nm;
        this.irdnt_cpcty = irdnt_cpcty;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public String getIrdnt_nm() {
        return irdnt_nm;
    }

    public String getIrdnt_cpcty() {
        return irdnt_cpcty;
    }
}

