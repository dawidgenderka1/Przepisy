package com.example.przepisy;

public class Rating {
    private int RecipeID;
    private String Username;
    private int Stars;

    public Rating(int RecipeID, String Username, int Stars) {
        this.RecipeID = RecipeID;
        this.Username = Username;
        this.Stars = Stars;
    }

}

