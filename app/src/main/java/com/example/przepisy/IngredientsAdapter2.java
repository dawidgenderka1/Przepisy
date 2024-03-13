package com.example.przepisy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientsAdapter2 extends RecyclerView.Adapter<IngredientsAdapter2.ViewHolder> {
    private List<IngredientNameResponse> ingredientsList;
    private Context context;

    public IngredientsAdapter2(List<IngredientNameResponse> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public IngredientsAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter2.ViewHolder holder, int position) {
        IngredientNameResponse ingredient = ingredientsList.get(position);
        holder.ingredientName.setText(ingredient.getName());
        holder.quantity.setText(ingredient.getQuantity());

        holder.removeIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer ingredientIdToRemove = ingredient.getIngredientID();

                SessionManager.getInstance(context).removeIngredientId(ingredientIdToRemove);

                ingredientsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, ingredientsList.size());
            }
        });

        holder.boughtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> fridgeIngredientIds = SessionManager.getInstance(context).getFridgeIngredientIds();

                Integer ingredientIdToAdd = ingredient.getIngredientID();

                if (!fridgeIngredientIds.contains(ingredientIdToAdd)) {
                    fridgeIngredientIds.add(ingredientIdToAdd);

                    SessionManager.getInstance(context).setFridgeIngredientIds(fridgeIngredientIds);

                    //Toast.makeText(context, "Składnik dodany do lodówki", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, "Składnik jest już w lodówce", Toast.LENGTH_SHORT).show();
                }
                SessionManager.getInstance(context).removeIngredientId(ingredientIdToAdd);

                ingredientsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, ingredientsList.size());
            }
        });



    }


    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName, quantity;
        ImageView removeIngredientButton, boughtButton;

        public ViewHolder(View view) {
            super(view);
            ingredientName = view.findViewById(R.id.ingredientNameTextView);
            quantity = view.findViewById(R.id.ingredientQuantityTextView);
            removeIngredientButton = view.findViewById(R.id.removeIngredientButton);
            boughtButton = view.findViewById(R.id.boughtButton);

        }
    }

    public void updateData(List<IngredientNameResponse> newData) {
        ingredientsList.clear();
        ingredientsList.addAll(newData);
        notifyDataSetChanged();
    }
}

