package com.example.przepisy;

public class CheckOwnershipRequest {
    private String Username;
    private int RecipeID;

    public CheckOwnershipRequest(String Username, int RecipeID) {
        this.Username = Username;
        this.RecipeID = RecipeID;
    }

    // Gettery i settery
    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public int getRecipeId() {
        return RecipeID;
    }

    public void setRecipeId(int RecipeID) {
        this.RecipeID = RecipeID;
    }
}

