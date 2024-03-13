package com.example.przepisy.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.przepisy.FindRecipeIdRequest;
import com.example.przepisy.FindRecipeIdResponse;
import com.example.przepisy.R;
import com.example.przepisy.Recipe;
import com.example.przepisy.RecipeIngredientRequest;
import com.example.przepisy.RecipesAdapter;
import com.example.przepisy.SessionManager;
import com.example.przepisy.SpacesItemDecoration;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.IngredientNameResponse;
import com.example.przepisy.api.UserApiService;
import com.example.przepisy.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment2 extends Fragment {

    private FragmentDashboardBinding binding;
    private int help=0;
    private Bundle bundle = new Bundle();
    private View root;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        Spinner spinnerCuisineType = root.findViewById(R.id.spinnerCuisineType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.cuisine_types, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisineType.setAdapter(adapter2);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if(help==1)
                {
                    binding.formCreateRecipe.setVisibility(View.GONE);
                    binding.fabAddRecipe.setVisibility(View.VISIBLE);
                    binding.listOfRecipes.setVisibility(View.VISIBLE);
                    binding.recipesRecyclerView.setVisibility(View.VISIBLE);
                    help=0;

                }

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);








        RecipesAdapter adapter = new RecipesAdapter(new ArrayList<>(), new RecipesAdapter.RecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                binding.fabAddRecipe.setVisibility(View.GONE);
            }
            @Override
            public void onHideRecyclerView() {
                binding.recipesRecyclerView.setVisibility(View.GONE);
            }
        });

        binding.recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recipesRecyclerView.setAdapter(adapter);
        int spaceInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        binding.recipesRecyclerView.addItemDecoration(new SpacesItemDecoration(spaceInPixels));

        if (!SessionManager.getInstance(getContext()).isLoggedIn()) {
            binding.fabAddRecipe.setVisibility(View.GONE);
        }
        else{
            binding.fabAddRecipe.setVisibility(View.VISIBLE);
        }



        loadRecipes();

        binding.fabAddRecipe.setOnClickListener(view -> {
            binding.recipesRecyclerView.setVisibility(View.GONE);
            binding.listOfRecipes.setVisibility(View.GONE);
            binding.fabAddRecipe.setVisibility(View.GONE);
            binding.formCreateRecipe.setVisibility(View.VISIBLE);

            help=1;
        });

        binding.buttonReturn.setOnClickListener(view -> {
            binding.formCreateRecipe.setVisibility(View.GONE);
            binding.fabAddRecipe.setVisibility(View.VISIBLE);
            binding.listOfRecipes.setVisibility(View.VISIBLE);
            binding.recipesRecyclerView.setVisibility(View.VISIBLE);
            help=0;
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRecipe();
            }
        });



        binding.addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngredientView();
            }
        });


        return root;
    }

    private void addIngredientView() {
        View ingredientView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_item, null, false);
        Spinner ingredientNameSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);
        ImageView deleteButton = ingredientView.findViewById(R.id.removeIngredientButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ingredientsContainer.removeView(ingredientView);
            }
        });

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
                    ingredientNameSpinner.setAdapter(adapter);
                } else {
                    Log.e("DashboardFragment", "Błąd przy pobieraniu nazw składników");
                }
            }

            @Override
            public void onFailure(Call<List<IngredientNameResponse>> call, Throwable t) {
                Log.e("DashboardFragment", "Błąd połączenia: " + t.getMessage());
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, 0, marginInPixels);
        ingredientView.setLayoutParams(layoutParams);

        binding.ingredientsContainer.addView(ingredientView);
    }



    private void loadRecipes() {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecipesAdapter adapter = (RecipesAdapter) binding.recipesRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.updateData(response.body());
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
            }
        });
    }

    private void submitRecipe() {
        String title = binding.editTextRecipeTitle.getText().toString();
        String description = binding.editTextRecipeDescription.getText().toString();
        int cookingTime=0;

        String username = SessionManager.getInstance(getContext()).getUsername();
        try {
            cookingTime = Integer.parseInt(binding.editTextCookingTime.getText().toString());
        } catch (NumberFormatException e) {
            cookingTime = 0;
        }
        String cuisineType = binding.spinnerCuisineType.getSelectedItem().toString();
        String instructions = binding.editTextInstructions.getText().toString();

        Recipe newRecipe = new Recipe(username, title, description, cookingTime, cuisineType, instructions);

        final int cookingTime2 = cookingTime;

        UserApiService apiService = ApiClient.getUserService();
        Call<Void> call = apiService.addRecipe(newRecipe);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Przepis dodany pomyślnie", Toast.LENGTH_SHORT).show();
                    findRecipeIdAndAddIngredients(username,title,description,cookingTime2,cuisineType,instructions);
                    binding.formCreateRecipe.setVisibility(View.GONE);
                    binding.recipesRecyclerView.setVisibility(View.VISIBLE);
                    //loadRecipes();


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

    private void findRecipeIdAndAddIngredients(String username, String title, String description, int cookingTime, String cuisineType, String instructions) {
        UserApiService apiService = ApiClient.getUserService();
        //Log.e("testetesttetst", username+title+description);
        FindRecipeIdRequest request = new FindRecipeIdRequest(username, title, description);
        apiService.findRecipeId(request).enqueue(new Callback<FindRecipeIdResponse>() {
            @Override
            public void onResponse(Call<FindRecipeIdResponse> call, Response<FindRecipeIdResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int recipeId = response.body().getRecipeID();
                    addRecipeIngredient(recipeId);
                    RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                    bundle.putString("title", title);
                    bundle.putString("description", description);
                    bundle.putInt("recipeId", recipeId);
                    bundle.putInt("cookingTime", cookingTime);
                    bundle.putString("cuisineType", cuisineType);
                    bundle.putString("instruction", instructions);
                    bundle.putDouble("SredniaOcena", 0.0);
                    recipeDetailFragment.setArguments(bundle);


                    NavController navController = Navigation.findNavController(root);
                    navController.navigate(R.id.action_details, bundle);;



                } else {
                    Log.e("findRecipeId", "Nie udało się znaleźć przepisu");
                }
            }

            @Override
            public void onFailure(Call<FindRecipeIdResponse> call, Throwable t) {
                Log.e("findRecipeId", "Błąd połączenia: " + t.getMessage());
            }
        });
    }

    private void addRecipeIngredient(int recipeId) {
        UserApiService apiService = ApiClient.getUserService();

        for(int i = 0; i < binding.ingredientsContainer.getChildCount(); i++) {
            View ingredientView = binding.ingredientsContainer.getChildAt(i);
            Spinner ingredientNameSpinner = ingredientView.findViewById(R.id.ingredientNameSpinner);
            EditText quantityEditText = ingredientView.findViewById(R.id.ingredientQuantityEditText);

            String ingredientName = ingredientNameSpinner.getSelectedItem().toString();
            String quantity = quantityEditText.getText().toString();

            String currentLanguage = SessionManager.getInstance(getContext()).getLanguage();

            //addRecipeIngredient(recipeId, ingredientName, quantity);


            RecipeIngredientRequest request = new RecipeIngredientRequest(recipeId, ingredientName, quantity);

            apiService.addRecipeIngredient(request, currentLanguage).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("AddRecipeIngredient", "Błąd połączenia: " + t.getMessage());
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (binding.recipesRecyclerView.getVisibility() == View.GONE) {
            binding.recipesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}