package com.example.przepisy;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredientsList;
    private Context context;

    public IngredientsAdapter(List<Ingredient> ingredientsList) {
        this.context = context;
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
        int ingredientId = ingredient.getIngredientID();

        List<Integer> savedIngredientIds = SessionManager.getInstance(context).getFridgeIngredientIds();

        if (savedIngredientIds.contains(ingredientId)) {
            holder.imageView.setImageResource(R.drawable.baseline_check_24);
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_clear_24);
        }

        holder.ingredientName.setText(ingredient.getName());
        holder.ingredientQuantity.setText(ingredient.getQuantity());



    }


    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView ingredientName, ingredientQuantity;

        public ViewHolder(View view) {
            super(view);
            ingredientName = view.findViewById(R.id.ingredientNameTextView);
            ingredientQuantity = view.findViewById(R.id.ingredientQuantityTextView);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    public void updateData(List<Ingredient> newData) {
        ingredientsList.clear();
        ingredientsList.addAll(newData);
        notifyDataSetChanged();
    }
}

