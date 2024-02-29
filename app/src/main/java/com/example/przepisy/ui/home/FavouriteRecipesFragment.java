package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.przepisy.R;
import com.example.przepisy.Recipe;
import com.example.przepisy.RecipesAdapter;
import com.example.przepisy.SessionManager;
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
public class FavouriteRecipesFragment extends Fragment {

    private FragmentDashboardBinding binding;



    public FavouriteRecipesFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FavouriteRecipesFragment newInstance(String param1, String param2) {
        FavouriteRecipesFragment fragment = new FavouriteRecipesFragment();
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
        apiService.getFavorites(SessionManager.getInstance(getContext()).getUsername()).enqueue(new Callback<List<Recipe>>() {
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