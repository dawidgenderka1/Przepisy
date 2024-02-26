package com.example.przepisy;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.przepisy.api.ApiClient;
import com.example.przepisy.databinding.FragmentHome2Binding;
import com.example.przepisy.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.przepisy.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    int recipeid=-1;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        sessionManager = SessionManager.getInstance(getApplicationContext());
        //sessionManager.setLogin(false);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        if (sessionManager.isLoggedIn()) {
            navController.setGraph(R.navigation.mobile_navigation2);
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(R.menu.bottom_nav_menu2);
            Log.d("Zalogowany", "Zalogowany!!");
        } else {
            navController.setGraph(R.navigation.mobile_navigation);
            binding.navView.getMenu().clear();
            binding.navView.inflateMenu(R.menu.bottom_nav_menu);
            Log.d("Niezalogowany", "Niezalogowany!!");
        }




        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home2) {
                navController.navigate(R.id.navigation_home2);
                return true;
            } else if (itemId == R.id.navigation_dashboard2) {
                navController.navigate(R.id.navigation_dashboard2);
                return true;
            } else if (itemId == R.id.navigation_notifications2) {
                navController.navigate(R.id.navigation_notifications2);
                return true;
            }
            if (itemId == R.id.navigation_home) {
                navController.navigate(R.id.navigation_home);
                return true;
            } else if (itemId == R.id.navigation_dashboard) {
                navController.navigate(R.id.navigation_dashboard);
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                navController.navigate(R.id.navigation_notifications);
                return true;
            }
            // Dodaj tutaj obsługę dla pozostałych elementów, jeśli są

            return false; // Domyślne zachowanie dla nierozpoznanych elementów
        });

        String languageCode = mapLanguageToCode(sessionManager.getLanguage());
        Locale locale = new Locale(languageCode);
        updateLocale(locale);

        handleIntent(getIntent());
    }

    public void reloadActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String title = intent.getStringExtra("title");
            recipeid = intent.getIntExtra("recipeid", -1);
            Log.d("Zalogowany", String.valueOf(recipeid));
            Log.d("Zalogowany", String.valueOf(recipeid));
            Log.d("Zalogowany", String.valueOf(recipeid));
            String description = intent.getStringExtra("description");
            int cookingTime = intent.getIntExtra("cookingTime", -1);
            String cuisineType = intent.getStringExtra("cuisineType");
            String instruction = intent.getStringExtra("instruction");
            // Możesz pobrać więcej danych, jeśli są potrzebne

            if (recipeid != -1) {
                // Logika do otwarcia fragmentu ze szczegółami przepisu
                // Możesz użyć NavController do nawigacji
                //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main); // Upewnij się, że używasz właściwego ID dla NavHostFragment
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putInt("recipeid", recipeid);
                bundle.putString("description", description);
                bundle.putInt("cookingTime", cookingTime);
                bundle.putString("cuisineType", cuisineType);
                bundle.putString("instruction", instruction);
                // Dodaj inne dane do bundle, jeśli są potrzebne
                navController.navigate(R.id.action_details, bundle); // Zakładając, że masz odpowiednią akcję w grafie nawigacji
            }
        }
    }

    public void updateLocale(Locale newLocale) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = newLocale;
        res.updateConfiguration(conf, dm);
        //Intent intent = new Intent(getActivity(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //startActivity(intent);
    }

    private String mapLanguageToCode(String languageName) {
        switch (languageName) {
            case "Język polski":
                return "pl"; // Kod języka dla polskiego
            // Angielski zostanie pominięty, aby korzystać z domyślnych zasobów z folderu `values`
            default:
                return ""; // Nie ustawiamy języka, korzystamy z domyślnych zasobów
        }
    }



}