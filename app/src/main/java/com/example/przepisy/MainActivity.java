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
import com.example.przepisy.ui.notifications.NotificationsFragment;
import com.example.przepisy.ui.notifications.NotificationsFragment2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.przepisy.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NotificationsFragment.BottomNavRefreshListener, NotificationsFragment2.BottomNavRefreshListener {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    int recipeid=-1;
    NavHostFragment navHostFragment;
    NavController navController;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        sessionManager = SessionManager.getInstance(getApplicationContext());
        //sessionManager.setLogin(false);
        String theme = sessionManager.getTheme();
        if (theme.equals("Motyw jasny")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (theme.equals("Motyw ciemny")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        String languageCode = mapLanguageToCode(sessionManager.getLanguage());
        Locale locale = new Locale(languageCode);
        updateLocale(locale);

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }




        navView = binding.navView;
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

            return false;
        });



        handleIntent(getIntent());


    }


    public void refreshBottomNav() {
        navView.getMenu().clear();
        navView.inflateMenu(R.menu.bottom_nav_menu);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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

            String description = intent.getStringExtra("description");
            int cookingTime = intent.getIntExtra("cookingTime", -1);
            String cuisineType = intent.getStringExtra("cuisineType");
            String instruction = intent.getStringExtra("instruction");

            if (recipeid != -1) {
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putInt("recipeid", recipeid);
                bundle.putString("description", description);
                bundle.putInt("cookingTime", cookingTime);
                bundle.putString("cuisineType", cuisineType);
                bundle.putString("instruction", instruction);

                navController.navigate(R.id.action_details, bundle);
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
                return "pl";
            default:
                return "";
        }
    }



}