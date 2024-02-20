package com.example.przepisy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.przepisy.MainActivity;
import com.example.przepisy.SessionManager;
import com.example.przepisy.databinding.FragmentHome2Binding;
import com.example.przepisy.databinding.FragmentHomeBinding;

public class HomeFragment2 extends Fragment {

    private FragmentHome2Binding binding;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ViewModel i binding
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHome2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicjalizacja SessionManager
        sessionManager = SessionManager.getInstance(requireActivity().getApplicationContext());

        // Dodanie onClickListener do przycisku wylogowania
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ustawienie stanu logowania na false
                sessionManager.setLogin(false);

                // Przeładowanie aktywności lub przejście do innego fragmentu/aktywności
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).reloadActivity();
                }
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