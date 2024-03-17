package com.example.przepisy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.przepisy.MainActivity;
import com.example.przepisy.R;
import com.example.przepisy.SessionManager;
import com.example.przepisy.databinding.FragmentHome2Binding;
import com.example.przepisy.databinding.FragmentHomeBinding;

public class HomeFragment2 extends Fragment {

    private FragmentHome2Binding binding;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHome2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireActivity().getApplicationContext());

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setLogin(false);
                sessionManager.clearUsername();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).reloadActivity();
                }
            }
        });

        binding.shoppingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details2);
            }
        });

        binding.favouriteRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details3);
            }
        });

        binding.fridgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details4);
            }
        });

        binding.yourRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details5);
            }
        });


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}