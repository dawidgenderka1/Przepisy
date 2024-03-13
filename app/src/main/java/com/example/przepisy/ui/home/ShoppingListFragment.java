package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.przepisy.IngredientsAdapter2;
import com.example.przepisy.R;
import com.example.przepisy.SessionManager;
import com.example.przepisy.SpacesItemDecoration;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.IngredientNameResponse;
import com.example.przepisy.api.UserApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShoppingListFragment extends Fragment {
    private RecyclerView ingredientsShoppingListRecyclerView;
    private IngredientsAdapter2 ingredientsAdapter;
    private List<IngredientNameResponse> ingredientsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        ImageView boughtButton = view.findViewById(R.id.boughtButton);
        ingredientsShoppingListRecyclerView = view.findViewById(R.id.ingredientsShoppingListRecyclerView);
        ingredientsShoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int spaceInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        ingredientsShoppingListRecyclerView.addItemDecoration(new SpacesItemDecoration(spaceInPixels));

        ingredientsAdapter = new IngredientsAdapter2(ingredientsList);
        ingredientsShoppingListRecyclerView.setAdapter(ingredientsAdapter);

        boughtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TAG", "Przycisk kliknięty");

                SessionManager.getInstance(getContext()).clearIngredientIds();

                ingredientsList.clear();

                ingredientsAdapter.notifyDataSetChanged();
            }
        });

        loadAllIngredientNames();
        return view;
    }

    private void loadIngredientName(int ingredientId) {
        UserApiService apiService = ApiClient.getUserService();
        String language = SessionManager.getInstance(getContext()).getLanguage();
        apiService.getIngredientName(ingredientId, language).enqueue(new Callback<IngredientNameResponse>() {
            @Override
            public void onResponse(Call<IngredientNameResponse> call, Response<IngredientNameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ingredientsList.add(response.body());
                    ingredientsAdapter.notifyItemInserted(ingredientsList.size() - 1);
                } else {
                    Toast.makeText(getContext(), "Błąd podczas ładowania nazwy składnika", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IngredientNameResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllIngredientNames() {
        List<Integer> ingredientIds = SessionManager.getInstance(getContext()).getIngredientIds();
        for (int ingredientId : ingredientIds) {
            loadIngredientName(ingredientId);
        }
    }
}

