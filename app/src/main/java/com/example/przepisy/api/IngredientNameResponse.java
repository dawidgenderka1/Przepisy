package com.example.przepisy.api;

public class IngredientNameResponse {

    private String Name;
    private String Quantity;
    private int IngredientID;


    // Konstruktor, getter i setter
    public IngredientNameResponse(String Name, String Quantity) {
        this.Name = Name;
        this.Quantity = Quantity;
    }

    public IngredientNameResponse(String Name, String Quantity, int IngredientID) {
        this.Name = Name;
        this.Quantity = Quantity;
        this.IngredientID = IngredientID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String Quantity) {
        this.Quantity = Quantity;
    }

    public int getIngredientID() {
        return IngredientID;
    }

    public void setIngredientID(int IngredientID) {
        this.IngredientID = IngredientID;
    }
}

