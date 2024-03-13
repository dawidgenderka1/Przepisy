package com.example.przepisy;

public class FavouriteToggleRequest {
    private String Username;
    private int RecipeID;

    public FavouriteToggleRequest(String username, int recipeID) {
        Username = username;
        RecipeID = recipeID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getRecipeID() {
        return RecipeID;
    }

    public void setRecipeID(int recipeID) {
        RecipeID = recipeID;
    }
}

