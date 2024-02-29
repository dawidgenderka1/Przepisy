package com.example.przepisy;


import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    private int RecipeID;

    private int UserID;
    private String Username; // Nowe pole

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

    public Recipe(String Username, String Title, String Description, int CookingTime, String CuisineType, String Instrukcja) {
        this.Username = Username;
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

    protected Recipe(Parcel in) {
        RecipeID = in.readInt();
        UserID = in.readInt();
        Title = in.readString();
        Description = in.readString();
        CookingTime = in.readInt();
        CuisineType = in.readString();
        SumaOcen = in.readInt();
        LiczbaOcen = in.readInt();
        SredniaOcena = in.readDouble();
        Instrukcja = in.readString();
        Username = in.readString(); // Przykład odczytu dodatkowego pola
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RecipeID);
        dest.writeInt(UserID);
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeInt(CookingTime);
        dest.writeString(CuisineType);
        dest.writeInt(SumaOcen);
        dest.writeInt(LiczbaOcen);
        dest.writeDouble(SredniaOcena);
        dest.writeString(Instrukcja);
        dest.writeString(Username); // Przykład zapisu dodatkowego pola
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

}

