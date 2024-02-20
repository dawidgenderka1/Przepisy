package com.example.przepisy;


public class Recipe {

    private int RecipeID;

    private int UserID;

    private String Title;

    private String Description;

    private int CookingTime;

    private String CuisineType;

    private int SumaOcen;

    private int LiczbaOcen;

    private double SredniaOcena;

    private String Instrukcja;

    public Recipe(String Title, String Description, double SredniaOcena) {
        this.Title = Title;
        this.Description = Description;
        this.SredniaOcena = SredniaOcena;
    }

    public Recipe(int RecipeID, int UserID, String Title, String Description, int CookingTime, String CuisineType, int SumaOcen, int LiczbaOcen, double SredniaOcena, String Instrukcja) {
        this.RecipeID = RecipeID;
        this.UserID = UserID;
        this.Title = Title;
        this.Description = Description;
        this.CookingTime = CookingTime;
        this.CuisineType = CuisineType;
        this.SumaOcen = SumaOcen;
        this.LiczbaOcen = LiczbaOcen;
        this.SredniaOcena = SredniaOcena;
        this.Instrukcja = Instrukcja;
    }

    public Recipe(int UserID, String Title, String Description, int CookingTime, String CuisineType, String Instrukcja) {
        this.UserID = UserID;
        this.Title = Title;
        this.Description = Description;
        this.CookingTime = CookingTime;
        this.CuisineType = CuisineType;
        this.Instrukcja = Instrukcja;
    }

    // Gettery
    public String getTitle() {
        return Title;
    }

    public int getRecipeID() {
        return RecipeID;
    }

    public String getDescription() {
        return Description;
    }

    public String getCuisineType() {
        return CuisineType;
    }

    public String getInstrukcja() {
        return Instrukcja;
    }

    public int getCookingTime() {
        return CookingTime;
    }

    public double getSredniaOcena() {
        return SredniaOcena;
    }
}

