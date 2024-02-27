package com.example.przepisy;

public class Ingredient {
    private int IngredientID;
    private String Quantity;
    private String Name;

    // Gettery i settery

    public Ingredient(int IngredientID, String Quantity, String Name) {
        this.IngredientID = IngredientID;
        this.Quantity = Quantity;
        this.Name = Name;
    }

    public int getIngredientID() {
        return IngredientID;
    }

    public void setIngredientID(int ingredientID) {
        IngredientID = ingredientID;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

