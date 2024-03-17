package com.example.przepisy.ui.notifications;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.przepisy.MainActivity;
import com.example.przepisy.R;
import com.example.przepisy.SessionManager;
import com.example.przepisy.databinding.FragmentNotificationsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class NotificationsFragment2 extends Fragment {

    private FragmentNotificationsBinding binding;
    private int y = 0;
    private int x = 0;
    private String savedLanguage;
    private String savedTheme;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        root = binding.getRoot();


        setupLanguageSpinner();
        setupThemeSpinner();

        return root;
    }

    private void setupLanguageSpinner() {
        Spinner spinnerLanguage = binding.spinnerLanguage;
        ArrayAdapter<CharSequence> adapterLanguage = ArrayAdapter.createFromResource(getContext(),
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapterLanguage);

        savedLanguage = SessionManager.getInstance(getContext()).getLanguage();
        if (!savedLanguage.isEmpty()) {
            if(savedLanguage.equals("Język angielski"))
            {
                savedLanguage = "English";
            }
            int spinnerPosition = adapterLanguage.getPosition(savedLanguage);
            spinnerLanguage.setSelection(spinnerPosition);
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = parentView.getItemAtPosition(position).toString();
                y=y+1;
                if(selectedLanguage.equals("Polish") || selectedLanguage.equals("Język polski"))
                {
                    String selectedLanguage2 = "Język polski";
                    SessionManager.getInstance(getContext()).setLanguage(selectedLanguage2);
                    String languageCode = mapLanguageToCode(selectedLanguage2);
                    Locale locale = new Locale(languageCode);
                    if(y > 1)
                    {
                        updateLocale(locale);
                        if (refreshListener != null) {
                            refreshListener.refreshBottomNav();
                        }
                        if (SessionManager.getInstance(getContext()).isLoggedIn()){

                            Menu bottomNavMenu = ((MainActivity) getActivity()).binding.navView.getMenu();
                            MenuItem menuItem = bottomNavMenu.findItem(R.id.navigation_home2);
                            menuItem.setTitle(getString(R.string.title_home));
                            MenuItem menuItem2 = bottomNavMenu.findItem(R.id.navigation_dashboard2);
                            menuItem2.setTitle(getString(R.string.title_dashboard));
                            MenuItem menuItem3 = bottomNavMenu.findItem(R.id.navigation_notifications2);
                            menuItem3.setTitle(getString(R.string.title_notifications));
                        }
                        else {
                            Menu bottomNavMenu = ((MainActivity) getActivity()).binding.navView.getMenu();
                            MenuItem menuItem = bottomNavMenu.findItem(R.id.navigation_home);
                            menuItem.setTitle(getString(R.string.title_home));
                            MenuItem menuItem2 = bottomNavMenu.findItem(R.id.navigation_dashboard);
                            menuItem2.setTitle(getString(R.string.title_dashboard));
                            MenuItem menuItem3 = bottomNavMenu.findItem(R.id.navigation_notifications);
                            menuItem3.setTitle(getString(R.string.title_notifications));
                        }
                        NavController navController = Navigation.findNavController(root);
                        navController.navigate(R.id.action_details2);
                    }
                }
                if(selectedLanguage.equals("English") || selectedLanguage.equals("Język angielski"))
                {
                    String selectedLanguage2 = "Język angielski";
                    SessionManager.getInstance(getContext()).setLanguage(selectedLanguage2);
                    String languageCode = mapLanguageToCode(selectedLanguage2);
                    Locale locale = new Locale(languageCode);
                    if(y > 1)
                    {
                        updateLocale(locale);
                        if (refreshListener != null) {
                            refreshListener.refreshBottomNav();
                        }
                        if (SessionManager.getInstance(getContext()).isLoggedIn()){

                            Menu bottomNavMenu = ((MainActivity) getActivity()).binding.navView.getMenu();
                            MenuItem menuItem = bottomNavMenu.findItem(R.id.navigation_home2);
                            menuItem.setTitle(getString(R.string.title_home));
                            MenuItem menuItem2 = bottomNavMenu.findItem(R.id.navigation_dashboard2);
                            menuItem2.setTitle(getString(R.string.title_dashboard));
                            MenuItem menuItem3 = bottomNavMenu.findItem(R.id.navigation_notifications2);
                            menuItem3.setTitle(getString(R.string.title_notifications));
                        }
                        else {
                            Menu bottomNavMenu = ((MainActivity) getActivity()).binding.navView.getMenu();
                            MenuItem menuItem = bottomNavMenu.findItem(R.id.navigation_home);
                            menuItem.setTitle(getString(R.string.title_home));
                            MenuItem menuItem2 = bottomNavMenu.findItem(R.id.navigation_dashboard);
                            menuItem2.setTitle(getString(R.string.title_dashboard));
                            MenuItem menuItem3 = bottomNavMenu.findItem(R.id.navigation_notifications);
                            menuItem3.setTitle(getString(R.string.title_notifications));
                        }
                        NavController navController = Navigation.findNavController(root);
                        navController.navigate(R.id.action_details2);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void setupThemeSpinner() {
        Spinner spinnerTheme = binding.spinnerTheme;
        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(getContext(),
                R.array.theme_options, android.R.layout.simple_spinner_item);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(adapterTheme);

        savedTheme = SessionManager.getInstance(getContext()).getTheme();
        savedLanguage = SessionManager.getInstance(getContext()).getLanguage();

        if (!savedTheme.isEmpty()) {
            if(savedLanguage.equals("Język angielski"))
            {
                if(savedTheme.equals("Motyw ciemny"))
                {
                    savedTheme = "Night";
                }
                if(savedTheme.equals("Motyw jasny"))
                {
                    savedTheme = "Day";
                }
            }
            int spinnerPosition = adapterTheme.getPosition(savedTheme);
            spinnerTheme.setSelection(spinnerPosition);
        }

        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTheme = parentView.getItemAtPosition(position).toString();
                x=x+1;
                if(selectedTheme.equals("Day") || selectedTheme.equals("Motyw jasny"))
                {
                    String selectedTheme2 = "Motyw jasny";
                    SessionManager.getInstance(getContext()).setTheme(selectedTheme2);
                    if(x > 1) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
                if(selectedTheme.equals("Night") || selectedTheme.equals("Motyw ciemny"))
                {
                    String selectedTheme2 = "Motyw ciemny";
                    SessionManager.getInstance(getContext()).setTheme(selectedTheme2);
                    if(x > 1) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
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
            case "Polish":
                return "pl";
            default:
                return "";
        }
    }

    public interface BottomNavRefreshListener {
        void refreshBottomNav();
    }

    private BottomNavRefreshListener refreshListener;


    public void onAttach(Context context) {
        super.onAttach(context);

        //if (context instanceof BottomNavRefreshListener) {
        //    refreshListener = (BottomNavRefreshListener) context;
        //} else {
        //    throw new RuntimeException(context.toString()
        //            + " must implement BottomNavRefreshListener");
        //}
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}