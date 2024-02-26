package com.example.przepisy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.przepisy.Hash;
import com.example.przepisy.MainActivity;
import com.example.przepisy.SessionManager;
import com.example.przepisy.User;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.UserApiService;
import com.example.przepisy.databinding.FragmentHomeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sessionManager = SessionManager.getInstance(getActivity().getApplicationContext());




        // Ustawienie logiki przełączania formularzy
        setupFormSwitching();
        setupLoginAndRegisterActions();

        return root;
    }






    private void setupFormSwitching() {
        // Przyciski
        Button buttonShowRegisterForm = binding.buttonShowRegisterForm;
        Button buttonShowLoginForm = binding.buttonShowLoginForm;

        // Layouty formularzy
        LinearLayout loginForm = binding.loginForm;
        LinearLayout registerForm = binding.registerForm;

        // Pokaż formularz rejestracji
        buttonShowRegisterForm.setOnClickListener(v -> {
            loginForm.setVisibility(View.GONE);
            registerForm.setVisibility(View.VISIBLE);
        });

        // Powrót do formularza logowania
        buttonShowLoginForm.setOnClickListener(v -> {
            registerForm.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);
        });
    }

    private void setupLoginAndRegisterActions() {
        // Przykład dodawania akcji do przycisku logowania
        binding.buttonLogin.setOnClickListener(v -> {
            // Logika logowania
            loginUser();
        });

        // Przykład dodawania akcji do przycisku rejestracji
        binding.buttonRegister.setOnClickListener(v -> {
            // Logika rejestracji
            registerUser();
        });
    }

    private void loginUser() {
        String username = binding.loginUsername.getText().toString();
        String password = binding.loginPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Nazwa użytkownika i hasło są wymagane", Toast.LENGTH_SHORT).show();
            return;
        }

        password = Hash.hashPassword(password);

        User user = new User(username, password);
        UserApiService apiService = ApiClient.getUserService();
        apiService.loginUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Tutaj obsłuż pomyślne logowanie
                    Toast.makeText(getContext(), "Logowanie udane", Toast.LENGTH_SHORT).show();
                    sessionManager.setLogin(true);
                    sessionManager.setUsername(username);
                    // Przeładowanie aktywności lub przejście do innego fragmentu/aktywności
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).reloadActivity();
                    }
                } else {
                    // Obsłuż błąd logowania (np. niepoprawne dane logowania)
                    Toast.makeText(getContext(), "Błąd logowania", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Obsłuż błąd połączenia z serwerem
                Toast.makeText(getContext(), "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void registerUser() {
        // Tutaj wprowadź logikę rejestracji
        String username = binding.registerUsername.getText().toString();
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String confirmPassword = binding.registerConfirmPassword.getText().toString();

        // Sprawdź, czy hasła się zgadzają, i wykonaj rejestrację
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Hasła się nie zgadzają.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(getContext(), "Błędny e-mail.", Toast.LENGTH_SHORT).show();
            return;
        }



        password = Hash.hashPassword(password);

        User newUser = new User(username, email, password); // Upewnij się, że konstruktor klasy User pasuje do tego

        UserApiService apiService = ApiClient.getUserService();
        apiService.addUser(newUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Rejestracja pomyślna", Toast.LENGTH_SHORT).show();
                    sessionManager.setLogin(true);
                    sessionManager.setUsername(username);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).reloadActivity();
                    }
                    // Opcjonalnie: Przełącz na widok logowania lub profil użytkownika
                } else {
                    Toast.makeText(getContext(), "Błąd rejestracji", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isValidEmail(String email) {
        // Proste wyrażenie regularne sprawdzające obecność znaku "@" oraz podstawową strukturę e-maila
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if (email == null) {
            return false;
        }

        return email.matches(emailRegex);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
