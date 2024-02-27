package com.example.przepisy;

public class FindRecipeIdResponse {
    private int RecipeID;

    // Konstruktor, getter i setter
    public FindRecipeIdResponse(int recipeID) {
        RecipeID = recipeID;
    }

    public int getRecipeID() {
        return RecipeID;
    }

    public void setRecipeID(int recipeID) {
        RecipeID = recipeID;
    }
}

