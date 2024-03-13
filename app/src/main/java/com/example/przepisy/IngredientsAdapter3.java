package com.example.przepisy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.UserApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientsAdapter3 extends RecyclerView.Adapter<IngredientsAdapter3.ViewHolder> {
    private List<String> ingredientsList;
    private Context context;

    public IngredientsAdapter3(List<String> ingredientsList, Context context) {
        this.ingredientsList = ingredientsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        loadIngredients(holder.ingredientNameSpinner);
        holder.removeIngredientButton.setOnClickListener(null);

        holder.removeIngredientButton.setVisibility(View.GONE);

        if (position == getItemCount() - 1) {
            holder.removeIngredientButton.setVisibility(View.VISIBLE);
            holder.removeIngredientButton.setOnClickListener(v -> {
                ingredientsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, ingredientsList.size());


            });
        }
    }


    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    private void loadIngredients(Spinner spinner) {
        UserApiService apiService = ApiClient.getUserService();
        String currentLanguage = SessionManager.getInstance(context).getLanguage();

        apiService.getIngredients(currentLanguage.equals("Język angielski") ? "en" : "pl").enqueue(new Callback<List<IngredientNameResponse>>() {
            @Override
            public void onResponse(Call<List<IngredientNameResponse>> call, Response<List<IngredientNameResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> ingredientNames = new ArrayList<>();
                    for (IngredientNameResponse ingredient : response.body()) {
                        ingredientNames.add(ingredient.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, ingredientNames);

                    spinner.setAdapter(adapter);

                } else {
                    Log.e("IngredientsAdapter", "Błąd przy pobieraniu nazw składników");
                }
            }


            @Override
            public void onFailure(Call<List<IngredientNameResponse>> call, Throwable t) {
                Log.e("IngredientsAdapter", "Błąd połączenia: " + t.getMessage());
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Spinner ingredientNameSpinner;
        public Button removeIngredientButton;

        public ViewHolder(View view) {
            super(view);
            ingredientNameSpinner = view.findViewById(R.id.ingredientNameSpinner);
            removeIngredientButton = view.findViewById(R.id.removeIngredientButton);
        }
    }
}

