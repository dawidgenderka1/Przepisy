package com.example.przepisy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.przepisy.ui.dashboard.RecipeDetailFragment;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private final List<Recipe> recipesList;

    private final RecipeClickListener clickListener;

    public RecipesAdapter(List<Recipe> recipesList, RecipeClickListener clickListener) {
        this.recipesList = recipesList;
        this.clickListener = clickListener;
    }

    public interface RecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onHideRecyclerView();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipesList.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());
        holder.averageRatingTextView.setText(String.valueOf(recipe.getSredniaOcena()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onHideRecyclerView();

                RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", recipe.getTitle());
                bundle.putString("description", recipe.getDescription());
                bundle.putInt("cookingTime", recipe.getCookingTime());
                bundle.putString("cuisineType", recipe.getCuisineType());
                bundle.putString("instruction", recipe.getInstrukcja());
                bundle.putInt("recipeId", recipe.getRecipeID());
                bundle.putDouble("SredniaOcena", recipe.getSredniaOcena());
                recipeDetailFragment.setArguments(bundle);

                try {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_details, bundle);
                }catch(Exception e) {

                    ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, recipeDetailFragment)
                            .addToBackStack(null)
                           .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipesList.size();
    }

    public void updateData(List<Recipe> newData) {
        recipesList.clear();
        recipesList.addAll(newData);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, averageRatingTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
            averageRatingTextView = view.findViewById(R.id.averageRatingTextView);
        }
    }
}
