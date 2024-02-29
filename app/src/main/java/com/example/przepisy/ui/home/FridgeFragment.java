package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.przepisy.Ingredient;
import com.example.przepisy.IngredientItem;
import com.example.przepisy.IngredientsAdapter3;
import com.example.przepisy.R;
import com.example.przepisy.Recipe;
import com.example.przepisy.RecipesAdapter;
import com.example.przepisy.SessionManager;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.IngredientNameResponse;
import com.example.przepisy.api.UserApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FridgeFragment extends Fragment {

    private LinearLayout ingredientsContainer;
    private Button addIngredientButton, showIngredientsButton, randomRecipeButton;
    StringBuilder selectedIngredients;
    private ArrayList<String> ingredientNames = new ArrayList<>();
    private List<Recipe> recipesList = new ArrayList<>();


    private StringBuilder ingredientsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);

        ingredientsContainer = view.findViewById(R.id.ingredientContainer);
        addIngredientButton = view.findViewById(R.id.addButton);
        showIngredientsButton = view.findViewById(R.id.showIngredientsButton);
        randomRecipeButton = view.findViewById(R.id.randomRecipeButton);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FridgeFragment", "addButton clicked");
                addIngredientView();
            }
        });

        showIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zbierz wybrane składniki do listy ingredientNames
                Toast.makeText(getContext(), recipesList.toString(), Toast.LENGTH_SHORT).show();
                showSelectedIngredients();

                // Teraz możesz zbudować ingredientsText z aktualnej listy ingredientNames
                ingredientsText = new StringBuilder();
                for (String name : ingredientNames) {
                    if (ingredientsText.length() > 0) {
                        ingredientsText.append(",");
                    }
                    ingredientsText.append(name);
                }
               if (!ingredientsText.toString().isEmpty()) {
                    loadRecipes(); // Wywołaj loadRecipes tylko jeśli ingredientsText nie jest puste
                } else {
                    //Toast.makeText(getContext(), "Brak składników", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getContext(), recipesList.toString(), Toast.LENGTH_SHORT).show();
                }
        });

        randomRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zbierz wybrane składniki do listy ingredientNames
                Toast.makeText(getContext(), recipesList.toString(), Toast.LENGTH_SHORT).show();
                showSelectedIngredients();

                // Teraz możesz zbudować ingredientsText z aktualnej listy ingredientNames
                ingredientsText = new StringBuilder();
                for (String name : ingredientNames) {
                    if (ingredientsText.length() > 0) {
                        ingredientsText.append(",");
                    }
                    ingredientsText.append(name);
                }
                if (!ingredientsText.toString().isEmpty()) {
                    loadRecipes2(); // Wywołaj loadRecipes tylko jeśli ingredientsText nie jest puste
                } else {
                    //Toast.makeText(getContext(), "Brak składników", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getContext(), recipesList.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void addIngredientView() {
        // Stwórz nowy widok składnika z 'ingredient_item2.xml'
        View ingredientView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_item2, null, false);
        Spinner ingredientNameSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);
        Button deleteButton = ingredientView.findViewById(R.id.removeIngredientButton);

        // Ustaw OnClickListener dla przycisku "Usuń"
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Usuń widok składnika z ingredientsContainer
                ingredientsContainer.removeView(ingredientView);
            }
        });

        // Pobierz nazwy składników z bazy danych i dodaj je do Spinnera
        loadIngredients(ingredientNameSpinner);

        // Dodaj widok składnika do ingredientsContainer
        ingredientsContainer.addView(ingredientView);
    }

    private void loadIngredients(Spinner spinner) {
        UserApiService apiService = ApiClient.getUserService();
        String currentLanguage = SessionManager.getInstance(getContext()).getLanguage();
        apiService.getIngredients(currentLanguage).enqueue(new Callback<List<IngredientNameResponse>>() {
            @Override
            public void onResponse(Call<List<IngredientNameResponse>> call, Response<List<IngredientNameResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> ingredientNames = new ArrayList<>();
                    for (IngredientNameResponse ingredient : response.body()) {
                        ingredientNames.add(ingredient.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, ingredientNames);
                    spinner.setAdapter(adapter);
                } else {
                    Log.e("FridgeFragment", "Błąd przy pobieraniu nazw składników");
                }
            }

            @Override
            public void onFailure(Call<List<IngredientNameResponse>> call, Throwable t) {
                Log.e("FridgeFragment", "Błąd połączenia: " + t.getMessage());
            }
        });
    }

    private void loadRecipes() {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRecipesUserCanCook(ingredientsText.toString(), SessionManager.getInstance(getContext()).getLanguage()).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipesList.clear(); // Wyczyść aktualną listę
                    recipesList.addAll(response.body());

                    if (!recipesList.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ingredientNames", ingredientNames);
                        NavController navController = Navigation.findNavController(getView()); // Użyj getView(), jeśli jesteś wewnątrz fragmentu
                        // Wykonaj akcję nawigacji
                        navController.navigate(R.id.action_details5, bundle);
                    } else {
                        // Opcjonalnie wyświetl komunikat informujący użytkownika, że lista jest pusta
                        Toast.makeText(getContext(), "Lista przepisów jest pusta", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Obsłuż błąd połączenia lub inny błąd
            }
        });
    }

    private void loadRecipes2() {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRecipesUserCanCook(ingredientsText.toString(), SessionManager.getInstance(getContext()).getLanguage()).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipesList.clear(); // Wyczyść aktualną listę
                    recipesList.addAll(response.body());

                    if (!recipesList.isEmpty()) {

                        // Przygotowanie listy do przekazania
                        ArrayList<Recipe> recipesToPass = new ArrayList<>(response.body());

// Przygotowanie Bundle
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("recipesList", recipesToPass);

// Nawigacja z Bundle
                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.action_details6, bundle);

                    } else {
                        // Opcjonalnie wyświetl komunikat informujący użytkownika, że lista jest pusta
                        Toast.makeText(getContext(), "Lista przepisów jest pusta", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Obsłuż błąd połączenia lub inny błąd
            }
        });
    }
    private void showSelectedIngredients() {
        ingredientNames.clear();

// Iteracja przez wszystkie widoki składnika w kontenerze
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);

            // Pobranie wybranej nazwy składnika ze Spinnera
            String selectedIngredientName = (String) ingredientSpinner.getSelectedItem();
            ingredientNames.add(selectedIngredientName);
        }
    }
}




