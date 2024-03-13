package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.przepisy.CheckOwnershipResponse;
import com.example.przepisy.FindRecipeIdRequest;
import com.example.przepisy.FindRecipeIdResponse;
import com.example.przepisy.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RandomRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przepisy.AlarmReceiver;
import com.example.przepisy.CheckFavouriteResponse;
import com.example.przepisy.Comment;
import com.example.przepisy.CommentsAdapter;
import com.example.przepisy.FavouriteToggleRequest;
import com.example.przepisy.Ingredient;
import com.example.przepisy.IngredientsAdapter;
import com.example.przepisy.MainActivity;
import com.example.przepisy.Note;
import com.example.przepisy.NoteResponse;
import com.example.przepisy.R;
import com.example.przepisy.Rating;
import com.example.przepisy.RatingResponse;
import com.example.przepisy.Recipe;
import com.example.przepisy.SessionManager;
import com.example.przepisy.api.ApiClient;
import com.example.przepisy.api.UserApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomRecipeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int recipeid = -1;
    private String title;
    private String description;
    int cookingTime;
    private double cuisineType;
    private String instruction;
    private String mParam1;
    private String mParam2;
    private RecyclerView commentsRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private EditText commentEditText;
    private Spinner ratingSpinner;
    private ImageView sendCommentButton;
    private ImageView addShoppingButton;
    private EditText noteEditText;
    private ImageView saveNoteButton;
    private View view;
    private ImageView imageView;
    private ImageView deleteRecipe;

    public RandomRecipeFragment() {
    }
    public static com.example.przepisy.ui.home.RandomRecipeFragment newInstance(String param1, String param2) {
        com.example.przepisy.ui.home.RandomRecipeFragment fragment = new com.example.przepisy.ui.home.RandomRecipeFragment();
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
        view = inflater.inflate(R.layout.fragment_random_recipe, container, false);
        commentEditText = view.findViewById(R.id.commentEditText);
        sendCommentButton = view.findViewById(R.id.sendCommentButton);
        noteEditText = view.findViewById(R.id.noteEditText);
        saveNoteButton = view.findViewById(R.id.saveNoteButton);
        addShoppingButton = view.findViewById(R.id.addShoppingList);
        deleteRecipe = view.findViewById(R.id.deleteRecipe);

        ratingSpinner = view.findViewById(R.id.ratingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(adapter);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details7);


            }
        };

        deleteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe(SessionManager.getInstance(getContext()).getUsername(),title, description);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_details10);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int rating = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    updateRating(recipeid, rating);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageView buttonSetAlarm = view.findViewById(R.id.button_set_alarm);
        buttonSetAlarm.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePicker;
            timePicker = new TimePickerDialog(getActivity(), (view, hourOfDay, minuteOfHour) -> {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minuteOfHour);
                scheduleNotification(selectedTime);
            }, hour, minute, true);
            timePicker.setTitle("Wybierz godzinę powiadomienia");
            timePicker.show();
        });

        imageView = view.findViewById(R.id.yourImageViewId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getTag() != null && imageView.getTag().equals("full")) {
                    imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                    imageView.setTag("empty");
                } else {
                    imageView.setImageResource(R.drawable.baseline_favorite_24);
                    imageView.setTag("full");
                }
                toggleFavorite(recipeid);
            }
        });

        if (!SessionManager.getInstance(getContext()).isLoggedIn()) {
            commentEditText.setVisibility(View.GONE);
            sendCommentButton.setVisibility(View.GONE);
            ratingSpinner.setVisibility(View.GONE);
            noteEditText.setVisibility(View.GONE);
            saveNoteButton.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            addShoppingButton.setVisibility(View.GONE);
        }
        else{
            commentEditText.setVisibility(View.VISIBLE);
            sendCommentButton.setVisibility(View.VISIBLE);
            ratingSpinner.setVisibility(View.VISIBLE);
            noteEditText.setVisibility(View.VISIBLE);
            saveNoteButton.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            addShoppingButton.setVisibility(View.VISIBLE);
        }




        ArrayList<Recipe> recipesList = getArguments().getParcelableArrayList("recipesList");
        if (recipesList != null && !recipesList.isEmpty()) {
            Recipe firstRecipe = recipesList.get(0);
            title = firstRecipe.getTitle();
            recipeid = firstRecipe.getRecipeID();
            description = firstRecipe.getDescription();
            cookingTime = firstRecipe.getCookingTime();
            cuisineType = firstRecipe.getSredniaOcena();
            instruction = firstRecipe.getInstrukcja();
            Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
            ((TextView) view.findViewById(R.id.recipeTitle)).setText(title);
            ((TextView) view.findViewById(R.id.recipeDescription)).setText(description);
            ((TextView) view.findViewById(R.id.recipeCookingTime)).setText(String.valueOf(cookingTime));
            ((TextView) view.findViewById(R.id.recipeCuisineType)).setText(String.valueOf(cuisineType));
            ((TextView) view.findViewById(R.id.recipeInstruction)).setText(instruction);
        }



        checkIfRecipeIsFavorite(recipeid);

        sendCommentButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString();
            if (!commentText.isEmpty()) {
                createComment(recipeid, commentText);
            } else {
                Toast.makeText(getContext(), "Komentarz nie może być pusty", Toast.LENGTH_SHORT).show();
            }
        });

        saveNoteButton.setOnClickListener(v -> {
            String noteText = noteEditText.getText().toString();
            if (!noteText.isEmpty()) {
                createNote(recipeid, noteText);
            } else {
                Toast.makeText(getContext(), "Notatka jest pusta", Toast.LENGTH_SHORT).show();
            }
        });

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(new CommentsAdapter(new ArrayList<>()));

        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsRecyclerView.setAdapter(new IngredientsAdapter(new ArrayList<>()));

        checkRecipeOwnership(SessionManager.getInstance(getContext()).getUsername(),recipeid);







        fetchAndSetRating(recipeid, SessionManager.getInstance(getContext()).getUsername());
        fetchAndSetNote(recipeid, SessionManager.getInstance(getContext()).getUsername());
        loadComments(recipeid, commentsRecyclerView);
        loadIngredients(recipeid, ingredientsRecyclerView);

        addShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndSaveIngredientIds(recipeid);
                List<Integer> ingredientIds = SessionManager.getInstance(getActivity()).getIngredientIds();

                String idsText = ingredientIds.stream().map(Object::toString).collect(Collectors.joining(", "));



            }
        });




        return view;
    }

    private void fetchAndSaveIngredientIds(int recipeId) {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getIngredientIdsByRecipe(recipeId).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SessionManager.getInstance(getContext()).setIngredientIds(response.body());
                } else {
                    Log.e("TAG", "Nie udało się pobrać ID składników: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Log.e("TAG", "Błąd połączenia", t);
            }
        });
    }

    private void checkRecipeOwnership(String username, int recipeId) {
        UserApiService apiService = ApiClient.getUserService();
        apiService.checkRecipeOwnership(username,recipeId).enqueue(new Callback<CheckOwnershipResponse>() {
            @Override
            public void onResponse(Call<CheckOwnershipResponse> call, Response<CheckOwnershipResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean belongsToUser = response.body().isBelongsToUser();

                    if (belongsToUser) {
                        deleteRecipe.setVisibility(View.VISIBLE);
                    } else {
                        deleteRecipe.setVisibility(View.GONE);
                    }
                } else {


                }
            }

            @Override
            public void onFailure(Call<CheckOwnershipResponse> call, Throwable t) {

            }
        });

    }


    private void deleteRecipe(String username, String title, String description) {
        UserApiService apiService = ApiClient.getUserService();
        FindRecipeIdRequest request = new FindRecipeIdRequest(username, title, description);
        apiService.deleteRecipe(request).enqueue(new Callback<FindRecipeIdResponse>() {
            @Override
            public void onResponse(Call<FindRecipeIdResponse> call, Response<FindRecipeIdResponse> response) {
                if (response.isSuccessful() && response.body() != null) {


                } else {
                    Log.e("findRecipeId", "Nie udało się znaleźć przepisu");
                }
            }

            @Override
            public void onFailure(Call<FindRecipeIdResponse> call, Throwable t) {
                Log.e("findRecipeId", "Błąd połączenia: " + t.getMessage());
            }
        });
    }

    private void scheduleNotification(Calendar selectedTime) {
        Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("recipeid", recipeid);
        intent.putExtra("description", description);
        intent.putExtra("cuisineType", cuisineType);
        intent.putExtra("cookingTime", cookingTime);
        intent.putExtra("instruction", instruction);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, selectedTime.getTimeInMillis(), pendingIntent);
    }


    private void createComment(int recipeId, String commentText) {
        UserApiService apiService = ApiClient.getUserService();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

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

    private void loadIngredients(int recipeId, RecyclerView ingredientsRecyclerView) {
        String currentLanguage = SessionManager.getInstance(getContext()).getLanguage();

        UserApiService apiService = ApiClient.getUserService();
        apiService.getIngredientsByRecipe(recipeId, currentLanguage).enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    IngredientsAdapter adapter = (IngredientsAdapter) ingredientsRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.updateData(response.body());
                    } else {
                        Log.e("RecipeDetailFragment", "Adapter nie jest zainicjalizowany");
                    }
                } else {
                    Log.e("RecipeDetailFragment", "Brak składników lub błąd: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                Log.e("RecipeDetailFragment", "Błąd połączenia: ", t);
            }
        });
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateRating(int recipeId, int rating) {
        Rating ratingData = new Rating(recipeId, SessionManager.getInstance(getContext()).getUsername(), rating);

        UserApiService apiService = ApiClient.getUserService();
        apiService.addRating(ratingData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ocena została zaktualizowana", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Nie udało się zaktualizować oceny", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndSetRating(int recipeId, String username) {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getRating(recipeId, username).enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int rating = response.body().getStars();
                    setRatingSpinnerPosition(rating);
                } else {
                    ratingSpinner.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {
                Log.e("RatingFetchError", "Error fetching rating: ", t);
                ratingSpinner.setSelection(0);
            }
        });
    }

    private void fetchAndSetNote(int recipeId, String username) {
        UserApiService apiService = ApiClient.getUserService();
        apiService.getNote(recipeId, username).enqueue(new Callback<NoteResponse>() {
            @Override
            public void onResponse(Call<NoteResponse> call, Response<NoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String noteText = response.body().getNoteText();
                    noteEditText.setText(noteText);
                } else {
                    noteEditText.setText("błąd");
                }
            }

            @Override
            public void onFailure(Call<NoteResponse> call, Throwable t) {
                Log.e("NoteFetchError", "Error fetching note: ", t);
                noteEditText.setText("");
            }
        });
    }


    private void setRatingSpinnerPosition(int rating) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) ratingSpinner.getAdapter();
        int position = adapter.getPosition(String.valueOf(rating));
        ratingSpinner.setSelection(position);
    }

    private void createNote(int recipeId, String noteText) {
        UserApiService apiService = ApiClient.getUserService();

        String username = SessionManager.getInstance(getContext()).getUsername();

        Note newNote = new Note(recipeId, username, noteText);
        apiService.addNote(newNote).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Notatka została dodany", Toast.LENGTH_SHORT).show();
                    hideKeyboardFrom(getContext(), view);

                } else {
                    Toast.makeText(getContext(), "Nie udało się dodać notatki", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorite(int recipeId) {
        String username = SessionManager.getInstance(getContext()).getUsername();
        UserApiService apiService = ApiClient.getUserService();
        FavouriteToggleRequest request = new FavouriteToggleRequest(username, recipeId);

        apiService.toggleFavorite(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Status ulubionych zmieniony", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Nie udało się zmienić statusu ulubionych", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfRecipeIsFavorite(int recipeId) {
        String username = SessionManager.getInstance(getContext()).getUsername();
        UserApiService apiService = ApiClient.getUserService();

        Call<CheckFavouriteResponse> call = apiService.checkFavorite(username, recipeId);
        call.enqueue(new Callback<CheckFavouriteResponse>() {
            @Override
            public void onResponse(Call<CheckFavouriteResponse> call, Response<CheckFavouriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body().isFavourite();
                    if (isFavorite) {
                        Log.d("CheckFavorite", "Przepis jest dodany do ulubionych.");
                        imageView.setImageResource(R.drawable.baseline_favorite_24);
                        imageView.setTag("full");
                    } else {
                        Log.d("CheckFavorite", "Przepis nie jest dodany do ulubionych.");
                        imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                        imageView.setTag("empty");
                    }
                } else {
                    Log.e("CheckFavorite", "Nie udało się sprawdzić ulubionych.");
                }
            }

            @Override
            public void onFailure(Call<CheckFavouriteResponse> call, Throwable t) {
                Log.e("CheckFavorite", "Błąd połączenia: " + t.getMessage());
            }
        });
    }









}