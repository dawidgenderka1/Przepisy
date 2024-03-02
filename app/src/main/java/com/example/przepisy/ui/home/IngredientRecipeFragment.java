package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.przepisy.R;
import com.example.przepisy.Recipe;
import com.example.przepisy.RecipesAdapter;
import com.example.przepisy.SessionManager;
import com.example.przepisy.SpacesItemDecoration;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.UserApiService;
import com.example.przepisy.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouriteRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientRecipeFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ArrayList<String> ingredientNames;
    private StringBuilder ingredientsText;



    public IngredientRecipeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static IngredientRecipeFragment newInstance(String param1, String param2) {
        IngredientRecipeFragment fragment = new IngredientRecipeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.action_details9);


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        if (getArguments() != null) {
            ingredientNames = getArguments().getStringArrayList("ingredientNames");
            ingredientsText = new StringBuilder();

            if (ingredientNames != null) {
                for (String name : ingredientNames) {
                    if (ingredientsText.length() > 0) {
                        ingredientsText.append(",");
                    }
                    ingredientsText.append(name);
                }
                Toast.makeText(getContext(), ingredientsText.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Brak składników", Toast.LENGTH_SHORT).show();
            }
            // Teraz masz listę nazw składników, którą możesz użyć w tym fragmencie
        }

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
        int spaceInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        binding.recipesRecyclerView.addItemDecoration(new SpacesItemDecoration(spaceInPixels));






        loadRecipes(); // Asynchroniczne ładowanie danych

        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Sprawdź, czy RecyclerView jest niewidoczny i jeśli tak, to go pokaż
        if (binding.recipesRecyclerView.getVisibility() == View.GONE) {
            binding.recipesRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    private void loadRecipes() {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRecipesUserCanCook(ingredientsText.toString(), SessionManager.getInstance(getContext()).getLanguage()).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Aktualizacja danych w adapterze
                    RecipesAdapter adapter = (RecipesAdapter) binding.recipesRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.updateData(response.body());
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




}