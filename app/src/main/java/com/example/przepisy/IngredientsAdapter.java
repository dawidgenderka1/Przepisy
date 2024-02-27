package com.example.przepisy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredientsList;

    public IngredientsAdapter(List<Ingredient> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientsList.get(position);
        // Ustawianie tekstu dla nazwy składnika i jego ilości
        holder.ingredientName.setText(ingredient.getName());
        holder.ingredientQuantity.setText(ingredient.getQuantity());
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName, ingredientQuantity;

        public ViewHolder(View view) {
            super(view);
            ingredientName = view.findViewById(R.id.ingredientNameTextView);
            ingredientQuantity = view.findViewById(R.id.ingredientQuantityTextView);
        }
    }

    public void updateData(List<Ingredient> newData) {
        ingredientsList.clear();
        ingredientsList.addAll(newData);
        notifyDataSetChanged();
    }
}

