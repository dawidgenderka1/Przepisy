package com.example.przepisy.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przepisy.Comment;
import com.example.przepisy.CommentsAdapter;
import com.example.przepisy.MainActivity;
import com.example.przepisy.R;
import com.example.przepisy.SessionManager;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.UserApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int recipeid = -1;
    private String mParam1;
    private String mParam2;
    RecyclerView commentsRecyclerView;
    EditText commentEditText;
    View view;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }
    public static RecipeDetailFragment newInstance(String param1, String param2) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        commentEditText = view.findViewById(R.id.commentEditText);
        Button sendCommentButton = view.findViewById(R.id.sendCommentButton);

        if (!SessionManager.getInstance(getContext()).isLoggedIn()) {
            // Użytkownik nie jest zalogowany, ukryj EditText i Button
            commentEditText.setVisibility(View.GONE);
            sendCommentButton.setVisibility(View.GONE);
        }
        else{
            commentEditText.setVisibility(View.VISIBLE);
            sendCommentButton.setVisibility(View.VISIBLE);
        }

        if (getArguments() != null) {
            String title = getArguments().getString("title");
            recipeid = getArguments().getInt("recipeId");
            String description = getArguments().getString("description");
            int cookingTime = getArguments().getInt("cookingTime");
            String cuisineType = getArguments().getString("cuisineType");
            String instruction = getArguments().getString("instruction");

            ((TextView) view.findViewById(R.id.recipeTitle)).setText(title);
            ((TextView) view.findViewById(R.id.recipeDescription)).setText(description);
            ((TextView) view.findViewById(R.id.recipeCookingTime)).setText(String.valueOf(cookingTime));
            ((TextView) view.findViewById(R.id.recipeCuisineType)).setText(cuisineType);
            ((TextView) view.findViewById(R.id.recipeInstruction)).setText(instruction);
        }

        sendCommentButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString();
            if (!commentText.isEmpty()) {
                createComment(recipeid, commentText);
            } else {
                Toast.makeText(getContext(), "Komentarz nie może być pusty", Toast.LENGTH_SHORT).show();
            }
        });

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Ustawienie pustego adaptera
        commentsRecyclerView.setAdapter(new CommentsAdapter(new ArrayList<>()));


        int recipeId = getArguments().getInt("recipeId", -1);

        loadComments(recipeId, commentsRecyclerView);

        return view;
    }

    private void createComment(int recipeId, String commentText) {
        // Tutaj logika tworzenia komentarza za pomocą API
        // Przykład użycia Retrofit do wysłania komentarza
        UserApiService apiService = ApiClient.getUserService();
        // Zakładając, że masz metodę w UserApiService do tworzenia komentarza
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

// Pobranie aktualnej daty i czasu
        String today = dateFormat.format(new Date());

        String username = SessionManager.getInstance(getContext()).getUsername();
        Log.d(String.valueOf(recipeId),String.valueOf(recipeId));

        Comment newComment = new Comment(recipeId, commentText, today, username);
        apiService.createComment(newComment).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Komentarz został dodany", Toast.LENGTH_SHORT).show();
                    commentEditText.setText("");
                    hideKeyboardFrom(getContext(), view);
                    loadComments(recipeId, commentsRecyclerView);

                    // Możesz odświeżyć listę komentarzy itd.
                } else {
                    Toast.makeText(getContext(), "Nie udało się dodać komentarza", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments(int recipeId, RecyclerView commentsRecyclerView) {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getComments(recipeId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentsAdapter adapter = (CommentsAdapter) commentsRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.updateData(response.body());
                    } else {
                        Log.e("RecipeDetailFragment", "Adapter nie jest zainicjalizowany");
                    }
                } else {
                    Log.e("RecipeDetailFragment", "Brak komentarzy lub błąd: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("RecipeDetailFragment", "Błąd połączenia: ", t);
            }
        });
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}