package com.example.przepisy.api;

public class IngredientNameResponse {

    private String Name;

    // Konstruktor, getter i setter
    public IngredientNameResponse(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
}

