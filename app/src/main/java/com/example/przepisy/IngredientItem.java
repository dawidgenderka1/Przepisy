package com.example.przepisy;

public class IngredientItem {
    private String ingredientName;
    private int selectedPosition = 0;

    public IngredientItem(String ingredientName) {
        this.ingredientName = ingredientName;
    }


    public IngredientItem() {
        this.ingredientName = "";
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}

