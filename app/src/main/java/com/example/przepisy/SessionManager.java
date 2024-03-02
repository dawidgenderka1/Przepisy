package com.example.przepisy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USERNAME = "username";
    private static final String USER_ID = "userId";
    private static final String LANGUAGE = "language"; // Dodane
    private static final String THEME = "theme"; // Dodane
    private static final String INGREDIENT_IDS = "ingredientIds";
    private static final String FRIDGE_INGREDIENT_IDS = "fridgeIds";



    // Prywatny konstruktor, aby zapobiec tworzeniu instancji z zewnątrz
    private SessionManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = prefs.edit();
    }

    // Metoda statyczna do uzyskania instancji
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(IS_LOGGED_IN, false);
    }

    public void setUsername(String username) {
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return prefs.getString(USERNAME, "");
    }

    public void setUserId(int userId) {
        editor.putInt(USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return prefs.getInt(USER_ID, -1); // Użyj wartości domyślnej, która wskazuje na brak userId
    }

    // Dodane metody dla języka
    public void setLanguage(String language) {
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage() {
        return prefs.getString(LANGUAGE, "Język polski"); // Domyślnie "Język polski"
    }

    // Dodane metody dla motywu
    public void setTheme(String theme) {
        editor.putString(THEME, theme);
        editor.apply();
    }

    public String getTheme() {
        return prefs.getString(THEME, "Motyw jasny"); // Domyślnie "Motyw jasny"
    }

    public void setIngredientIds(List<Integer> ingredientIds) {
        Gson gson = new Gson();
        String json = gson.toJson(ingredientIds);
        editor.putString(INGREDIENT_IDS, json);
        editor.apply();
    }

    // Dodaj metodę do odczytywania listy identyfikatorów składników
    public List<Integer> getIngredientIds() {
        Gson gson = new Gson();
        String json = prefs.getString(INGREDIENT_IDS, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        List<Integer> ingredientIds = gson.fromJson(json, type);
        return ingredientIds == null ? new ArrayList<>() : ingredientIds;
    }

    public void clearIngredientIds() {
        // Utwórz pustą listę
        List<Integer> emptyList = new ArrayList<>();
        Gson gson = new Gson();
        // Konwertuj pustą listę do formatu JSON
        String json = gson.toJson(emptyList);
        // Zapisz pusty JSON jako nową wartość dla INGREDIENT_IDS
        editor.putString(INGREDIENT_IDS, json);
        editor.apply();
    }


    public void removeIngredientId(Integer ingredientId) {
        // Pobierz aktualną listę identyfikatorów
        List<Integer> ingredientIds = getIngredientIds();

        // Usuń określony identyfikator, jeśli istnieje
        if (ingredientIds.contains(ingredientId)) {
            ingredientIds.remove(ingredientId);

            // Zapisz zmodyfikowaną listę z powrotem
            setIngredientIds(ingredientIds);
        }
    }

    public void setFridgeIngredientIds(List<Integer> ingredientIds) {
        Gson gson = new Gson();
        String json = gson.toJson(ingredientIds);
        editor.putString(FRIDGE_INGREDIENT_IDS, json);
        editor.apply();
    }

    // Dodaj metodę do odczytywania listy identyfikatorów składników
    public List<Integer> getFridgeIngredientIds() {
        Gson gson = new Gson();
        String json = prefs.getString(FRIDGE_INGREDIENT_IDS, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        List<Integer> ingredientIds = gson.fromJson(json, type);
        return ingredientIds == null ? new ArrayList<>() : ingredientIds;
    }

    public void removeFridgeIngredientId(Integer ingredientId) {
        // Pobierz aktualną listę identyfikatorów
        List<Integer> ingredientIds = getFridgeIngredientIds();

        // Usuń określony identyfikator, jeśli istnieje
        if (ingredientIds.contains(ingredientId)) {
            ingredientIds.remove(ingredientId);

            // Zapisz zmodyfikowaną listę z powrotem
            setFridgeIngredientIds(ingredientIds);
        }
    }

    public void clearFridgeIngredientIds() {
        // Utwórz pustą listę
        List<Integer> emptyList = new ArrayList<>();
        Gson gson = new Gson();
        // Konwertuj pustą listę do formatu JSON
        String json = gson.toJson(emptyList);
        // Zapisz pusty JSON jako nową wartość dla INGREDIENT_IDS
        editor.putString(FRIDGE_INGREDIENT_IDS, json);
        editor.apply();
    }

    public void addFridgeIngredientId(Integer ingredientId) {
        // Pobierz aktualną listę identyfikatorów
        List<Integer> ingredientIds = getFridgeIngredientIds();

        // Dodaj nowy identyfikator do listy, jeśli jeszcze nie istnieje
        if (!ingredientIds.contains(ingredientId)) {
            ingredientIds.add(ingredientId);

            // Zapisz zmodyfikowaną listę z powrotem
            setFridgeIngredientIds(ingredientIds);
        }
    }



    // Możesz dodać więcej metod do zarządzania sesją użytkownika
}
