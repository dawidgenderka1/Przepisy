package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.przepisy.IngredientIdResponse;
import com.example.przepisy.R;
import com.example.przepisy.Recipe;
import com.example.przepisy.SessionManager;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.IngredientNameResponse;
import com.example.przepisy.api.UserApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FridgeFragment extends Fragment {

    private LinearLayout ingredientsContainer;
    private Button addIngredientButton, showIngredientsButton, randomRecipeButton;

    private int help=0;
    private ArrayList<String> ingredientNames = new ArrayList<>();
    private List<Recipe> recipesList = new ArrayList<>();


    private StringBuilder ingredientsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);

        ingredientsContainer = view.findViewById(R.id.ingredientContainer);
        addIngredientButton = view.findViewById(R.id.addButton);
        showIngredientsButton = view.findViewById(R.id.showIngredientsButton);
        randomRecipeButton = view.findViewById(R.id.randomRecipeButton);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details8);


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        List<Integer> fridgeIngredientIds = SessionManager.getInstance(getContext()).getFridgeIngredientIds();

        for (Integer id : fridgeIngredientIds) {
            createIngredientSpinner(id,true);
        }

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);

            ingredientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(help==1) {
                        saveFridgeIngredientIds();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

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
                showSelectedIngredients();

                ingredientsText = new StringBuilder();
                for (String name : ingredientNames) {
                    if (ingredientsText.length() > 0) {
                        ingredientsText.append(",");
                    }
                    ingredientsText.append(name);
                }
               if (!ingredientsText.toString().isEmpty()) {
                    loadRecipes();
                } else {

                }

                }
        });

        randomRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSelectedIngredients();

                ingredientsText = new StringBuilder();
                for (String name : ingredientNames) {
                    if (ingredientsText.length() > 0) {
                        ingredientsText.append(",");
                    }
                    ingredientsText.append(name);
                }
                if (!ingredientsText.toString().isEmpty()) {
                    loadRecipes2();
                } else {

                }

            }
        });


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                help=1;
            }
        }, 500);




        return view;
    }

    private void addIngredientView() {

        View ingredientView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_item2, null, false);
        Spinner ingredientNameSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);
        ImageView deleteButton = ingredientView.findViewById(R.id.removeIngredientButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientsContainer.removeView(ingredientView);
            }
        });

        loadIngredients(ingredientNameSpinner);

        ingredientNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                saveFridgeIngredientIds();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, 0, marginInPixels);
        ingredientView.setLayoutParams(layoutParams);


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
                    recipesList.clear();
                    recipesList.addAll(response.body());

                    if (!recipesList.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ingredientNames", ingredientNames);
                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.action_details5, bundle);
                    } else {
                        Toast.makeText(getContext(), "Nie wystarczająca ilość składników", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {

            }
        });
    }

    private void loadRecipes2() {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRecipesUserCanCook(ingredientsText.toString(), SessionManager.getInstance(getContext()).getLanguage()).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipesList.clear();
                    recipesList.addAll(response.body());

                    if (!recipesList.isEmpty()) {

                        ArrayList<Recipe> recipesToPass = new ArrayList<>(response.body());

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("recipesList", recipesToPass);

                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.action_details6, bundle);

                    } else {
                        Toast.makeText(getContext(), "Nie wystarczająca ilość składników", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {

            }
        });
    }
    private void showSelectedIngredients() {
        ingredientNames.clear();

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);

            String selectedIngredientName = (String) ingredientSpinner.getSelectedItem();
            ingredientNames.add(selectedIngredientName);
        }
    }

    private void saveFridgeIngredientIds() {
        UserApiService apiService = ApiClient.getUserService();
        List<Integer> ingredientIds = new ArrayList<>();

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            Spinner ingredientSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);
            String ingredientName = ingredientSpinner.getSelectedItem().toString();

            apiService.getIngredientId(SessionManager.getInstance(getContext()).getLanguage(), ingredientName).enqueue(new Callback<IngredientIdResponse>() {
                @Override
                public void onResponse(Call<IngredientIdResponse> call, Response<IngredientIdResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ingredientIds.add(response.body().getIngredientID());

                        if (ingredientIds.size() == ingredientsContainer.getChildCount()) {
                            SessionManager.getInstance(getContext()).setFridgeIngredientIds(ingredientIds);

                        }
                    } else {
                        Log.e("FridgeFragment", "Nie udało się pobrać ID składnika: " + ingredientName);
                    }
                }

                @Override
                public void onFailure(Call<IngredientIdResponse> call, Throwable t) {
                    Log.e("FridgeFragment", "Błąd połączenia: " + t.getMessage());
                }
            });
        }
    }

    private void createIngredientSpinner(Integer ingredientId, boolean isRemovable) {
        View ingredientView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_item2, null, false);
        Spinner ingredientNameSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);
        ImageView deleteButton = ingredientView.findViewById(R.id.removeIngredientButton);

        if (!isRemovable) {
            deleteButton.setVisibility(View.GONE);
        }

        loadIngredientNameById(ingredientNameSpinner, ingredientId);

        deleteButton.setOnClickListener(v -> {
            ingredientsContainer.removeView(ingredientView);
            removeIngredientIdFromSession(ingredientId);
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, 0, marginInPixels);
        ingredientView.setLayoutParams(layoutParams);


        ingredientsContainer.addView(ingredientView);
    }

    private void loadIngredientNameById(final Spinner spinner, final Integer selectedIngredientId) {
        UserApiService apiService = ApiClient.getUserService();
        String currentLanguage = SessionManager.getInstance(getContext()).getLanguage();
        apiService.getIngredientName(selectedIngredientId, currentLanguage).enqueue(new Callback<IngredientNameResponse>() {
            @Override
            public void onResponse(Call<IngredientNameResponse> call, Response<IngredientNameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String selectedIngredientName = response.body().getName();
                    loadAllIngredients(spinner, selectedIngredientName);
                } else {
                    Log.e("FridgeFragment", "Nie udało się pobrać nazwy składnika");
                }
            }

            @Override
            public void onFailure(Call<IngredientNameResponse> call, Throwable t) {
                Log.e("FridgeFragment", "Błąd połączenia: " + t.getMessage());
            }
        });
    }

    private void loadAllIngredients(final Spinner spinner, final String selectedIngredientName) {
        UserApiService apiService = ApiClient.getUserService();
        String currentLanguage = SessionManager.getInstance(getContext()).getLanguage();


        apiService.getIngredients(currentLanguage).enqueue(new Callback<List<IngredientNameResponse>>() {
            @Override
            public void onResponse(Call<List<IngredientNameResponse>> call, Response<List<IngredientNameResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> ingredientNames = new ArrayList<>();
                    int selectedIndex = 0;
                    for (int i = 0; i < response.body().size(); i++) {
                        String ingredientName = response.body().get(i).getName();
                        ingredientNames.add(ingredientName);
                        if(ingredientName.equals(selectedIngredientName)) {
                            selectedIndex = i;
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, ingredientNames);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(selectedIndex);
                } else {
                    Log.e("FridgeFragment", "Nie udało się pobrać nazw składników");
                }
            }

            @Override
            public void onFailure(Call<List<IngredientNameResponse>> call, Throwable t) {
                Log.e("FridgeFragment", "Błąd połączenia: " + t.getMessage());
            }
        });
    }



    private void removeIngredientIdFromSession(Integer ingredientId) {
        List<Integer> currentIds = SessionManager.getInstance(getContext()).getFridgeIngredientIds();
        currentIds.remove(ingredientId);
        SessionManager.getInstance(getContext()).setFridgeIngredientIds(currentIds);
    }




}




