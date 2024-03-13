package com.example.przepisy.ui.dashboard;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        return root;
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



    @Override
    public void onResume() {
        super.onResume();
        if (binding.recipesRecyclerView.getVisibility() == View.GONE) {
            binding.recipesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}


