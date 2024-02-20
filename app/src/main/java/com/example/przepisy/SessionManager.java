package com.example.przepisy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USERNAME = "username";
    private static final String USER_ID = "userId";


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

    // Możesz dodać więcej metod do zarządzania sesją użytkownika
}
