package com.example.przepisy.ui.dashboard;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.przepisy.R;
import com.example.przepisy.Recipe;
import com.example.przepisy.RecipesAdapter;
import com.example.przepisy.SessionManager;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.UserApiService;
import com.example.przepisy.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment2 extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner spinnerCuisineType = root.findViewById(R.id.spinnerCuisineType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.cuisine_types, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisineType.setAdapter(adapter2);



        // Inicjalizacja RecyclerView z pustym adapterem
        RecipesAdapter adapter = new RecipesAdapter(new ArrayList<>(), new RecipesAdapter.RecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                // Tutaj możesz na przykład wyświetlić Toast z dodatkowymi informacjami
                Log.d("55", "index=");
            }
            @Override
            public void onHideRecyclerView() {
                binding.recipesRecyclerView.setVisibility(View.GONE); // Ukrywa RecyclerView
            }
        });

        binding.recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recipesRecyclerView.setAdapter(adapter);

        if (!SessionManager.getInstance(getContext()).isLoggedIn()) {
            // Użytkownik nie jest zalogowany, ukryj EditText i Button
            binding.fabAddRecipe.setVisibility(View.GONE);
        }
        else{
            binding.fabAddRecipe.setVisibility(View.VISIBLE);
        }

        Toast.makeText(getContext(), "hmmmmm", Toast.LENGTH_SHORT).show();

        loadRecipes(); // Asynchroniczne ładowanie danych

        binding.fabAddRecipe.setOnClickListener(view -> {
            // Ukryj RecyclerView i pokaż formularz
            binding.recipesRecyclerView.setVisibility(View.GONE);
            binding.formCreateRecipe.setVisibility(View.VISIBLE);
        });

        binding.buttonReturn.setOnClickListener(view -> {
            // Ukryj formularz i pokaż RecyclerView
            binding.formCreateRecipe.setVisibility(View.GONE);
            binding.recipesRecyclerView.setVisibility(View.VISIBLE);
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRecipe();
            }
        });

        return root;
    }

    private void loadRecipes() {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Aktualizacja danych w adapterze
                    RecipesAdapter adapter = (RecipesAdapter) binding.recipesRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.updateData(response.body());
                    }
                    Log.d("DashboardFragment", "Odebrane przepisy: " + response.body());

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Obsłuż błąd połączenia lub inny błąd
            }
        });
    }

    private void submitRecipe() {
        String title = binding.editTextRecipeTitle.getText().toString();
        String description = binding.editTextRecipeDescription.getText().toString();
        int cookingTime;
        int userID = 1;//DO ZMIANY
        try {
            cookingTime = Integer.parseInt(binding.editTextCookingTime.getText().toString());
        } catch (NumberFormatException e) {
            cookingTime = 0; // Domyślna wartość, jeśli pole jest puste lub nieprawidłowe
        }
        String cuisineType = binding.spinnerCuisineType.getSelectedItem().toString();
        String instructions = binding.editTextInstructions.getText().toString();

        Recipe newRecipe = new Recipe(userID, title, description, cookingTime, cuisineType, instructions);

        UserApiService apiService = ApiClient.getUserService();
        Call<Void> call = apiService.addRecipe(newRecipe);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Przepis dodany pomyślnie", Toast.LENGTH_SHORT).show();
                    // Opcjonalnie: schowaj formularz i odśwież listę przepisów
                    binding.formCreateRecipe.setVisibility(View.GONE);
                    binding.recipesRecyclerView.setVisibility(View.VISIBLE);
                    loadRecipes();
                } else {
                    Toast.makeText(getContext(), "Nie udało się dodać przepisu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // Sprawdź, czy RecyclerView jest niewidoczny i jeśli tak, to go pokaż
        if (binding.recipesRecyclerView.getVisibility() == View.GONE) {
            binding.recipesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}