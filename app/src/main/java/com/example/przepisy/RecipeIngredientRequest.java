package com.example.przepisy;

public class RecipeIngredientRequest {
    private int RecipeID;
    private String IngredientName;
    private String Quantity;

    // Konstruktor
    public RecipeIngredientRequest(int RecipeID, String IngredientName, String Quantity) {
        this.RecipeID = RecipeID;
        this.IngredientName = IngredientName;
        this.Quantity = Quantity;
    }

    // Gettery i settery
    public int getRecipeID() {
        return RecipeID;
    }

    public void setRecipeID(int RecipeID) {
        this.RecipeID = RecipeID;
    }

    public String getIngredientName() {
        return IngredientName;
    }

    public void setIngredientName(String IngredientName) {
        this.IngredientName = IngredientName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String Quantity) {
        this.Quantity = Quantity;
    }
}
