package com.example.przepisy.ui.notifications;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.przepisy.MainActivity;
import com.example.przepisy.R;
import com.example.przepisy.SessionManager;
import com.example.przepisy.databinding.FragmentNotificationsBinding;

import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private int y = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Ustawienie spinnerów
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

        String savedLanguage = SessionManager.getInstance(getContext()).getLanguage();
        if (!savedLanguage.isEmpty()) {
            int spinnerPosition = adapterLanguage.getPosition(savedLanguage);
            spinnerLanguage.setSelection(spinnerPosition);
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = parentView.getItemAtPosition(position).toString();
                SessionManager.getInstance(getContext()).setLanguage(selectedLanguage);
                String languageCode = mapLanguageToCode(selectedLanguage);
                Locale locale = new Locale(languageCode);
                y=y+1;
                if(y == 2)
                {
                    updateLocale(locale);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Kod do wykonania, gdy nic nie jest wybrane
            }
        });
    }

    private void setupThemeSpinner() {
        Spinner spinnerTheme = binding.spinnerTheme;
        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(getContext(),
                R.array.theme_options, android.R.layout.simple_spinner_item);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(adapterTheme);

        String savedTheme = SessionManager.getInstance(getContext()).getTheme();
        if (!savedTheme.isEmpty()) {
            int spinnerPosition = adapterTheme.getPosition(savedTheme);
            spinnerTheme.setSelection(spinnerPosition);
        }

        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTheme = parentView.getItemAtPosition(position).toString();
                SessionManager.getInstance(getContext()).setTheme(selectedTheme);
                // Tutaj można dodać logikę zmiany motywu w aplikacji

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Kod do wykonania, gdy nic nie jest wybrane
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
                return "pl"; // Kod języka dla polskiego
            // Angielski zostanie pominięty, aby korzystać z domyślnych zasobów z folderu `values`
            default:
                return ""; // Nie ustawiamy języka, korzystamy z domyślnych zasobów
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}