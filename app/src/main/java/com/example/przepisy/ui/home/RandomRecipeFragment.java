package com.example.przepisy.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private String cuisineType;
    private String instruction;
    private String mParam1;
    private String mParam2;
    private RecyclerView commentsRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private EditText commentEditText;
    private Spinner ratingSpinner;
    private Button sendCommentButton;
    private Button addShoppingButton;
    private EditText noteEditText;
    private Button saveNoteButton;
    private View view;
    private ImageView imageView;

    public RandomRecipeFragment() {
        // Required empty public constructor
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

        ratingSpinner = view.findViewById(R.id.ratingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(adapter);

        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Zakładając, że "Oceń" jest na pozycji 0
                    int rating = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    // Aktualizacja oceny w bazie danych
                    updateRating(recipeid, rating);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Można zignorować
            }
        });

        Button buttonSetAlarm = view.findViewById(R.id.button_set_alarm);
        buttonSetAlarm.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePicker;
            timePicker = new TimePickerDialog(getActivity(), (view, hourOfDay, minuteOfHour) -> {
                // Tutaj zapisz wybrany czas i zaplanuj alarm
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
                // Sprawdź, który obrazek jest obecnie ustawiony
                if (imageView.getTag() != null && imageView.getTag().equals("full")) {
                    // Jeśli serce jest pełne, zmień na puste
                    imageView.setImageResource(R.drawable.heart_empty);
                    imageView.setTag("empty"); // Ustaw tag, aby śledzić aktualny stan obrazka
                } else {
                    // Jeśli serce jest puste, zmień na pełne
                    imageView.setImageResource(R.drawable.heart_full);
                    imageView.setTag("full"); // Ustaw tag, aby śledzić aktualny stan obrazka
                }
                toggleFavorite(recipeid);
            }
        });

        if (!SessionManager.getInstance(getContext()).isLoggedIn()) {
            // Użytkownik nie jest zalogowany, ukryj EditText i Button
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
            // Przykład użycia pierwszego przepisu z listy
            Recipe firstRecipe = recipesList.get(0); // lub użyj pętli, aby obsłużyć wszystkie przepisy
            title = firstRecipe.getTitle();
            recipeid = firstRecipe.getRecipeID();
            description = firstRecipe.getDescription();
            cookingTime = firstRecipe.getCookingTime();
            cuisineType = firstRecipe.getCuisineType();
            instruction = firstRecipe.getInstrukcja();
            Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
            // Użyj tych zmiennych do aktualizacji UI
            ((TextView) view.findViewById(R.id.recipeTitle)).setText(title);
            ((TextView) view.findViewById(R.id.recipeDescription)).setText(description);
            ((TextView) view.findViewById(R.id.recipeCookingTime)).setText(String.valueOf(cookingTime));
            ((TextView) view.findViewById(R.id.recipeCuisineType)).setText(cuisineType);
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
        // Ustawienie pustego adaptera
        commentsRecyclerView.setAdapter(new CommentsAdapter(new ArrayList<>()));

        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Ustawienie pustego adaptera
        ingredientsRecyclerView.setAdapter(new IngredientsAdapter(new ArrayList<>()));







        //int recipeId = getArguments().getInt("recipeId", -1);
        fetchAndSetRating(recipeid, SessionManager.getInstance(getContext()).getUsername());
        fetchAndSetNote(recipeid, SessionManager.getInstance(getContext()).getUsername());
        loadComments(recipeid, commentsRecyclerView);
        loadIngredients(recipeid, ingredientsRecyclerView);

        addShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndSaveIngredientIds(recipeid);
                // Pobierz listę ID składników za pomocą SessionManager
                List<Integer> ingredientIds = SessionManager.getInstance(getActivity()).getIngredientIds();

// Przekształć listę ID na ciąg tekstowy
                String idsText = ingredientIds.stream().map(Object::toString).collect(Collectors.joining(", "));

// Wyświetl ciąg tekstowy jako Toast
                Toast.makeText(getActivity(), "Zapisane ID składników: " + idsText, Toast.LENGTH_LONG).show();

            }
        });




        return view;
    }

    private void fetchAndSaveIngredientIds(int recipeId) {
        UserApiService apiService = ApiClient.getUserService(); // Uzyskaj instancję twojego API
        apiService.getIngredientIdsByRecipe(recipeId).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Zapisz pobrane ID składników w SessionManager
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

    private void scheduleNotification(Calendar selectedTime) {
        Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("recipeid", recipeid);
        intent.putExtra("description", description);
        intent.putExtra("cuisineType", cuisineType);
        intent.putExtra("cookingTime", cookingTime);
        intent.putExtra("instruction", instruction);
        // Możesz dodać dodatkowe dane do Intentu, jeśli chcesz przekazać do powiadomienia
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, selectedTime.getTimeInMillis(), pendingIntent);
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

    private void loadIngredients(int recipeId, RecyclerView ingredientsRecyclerView) {
        // Pobranie aktualnego języka z SessionManagera
        String currentLanguage = SessionManager.getInstance(getContext()).getLanguage();

        // Wywołanie API z dodatkowym parametrem dla języka
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
        // Przygotuj dane oceny
        Rating ratingData = new Rating(recipeId, SessionManager.getInstance(getContext()).getUsername(), rating);

        // Użyj Retrofit do aktualizacji oceny
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
                    // Ustaw spinner na odpowiedniej pozycji
                    setRatingSpinnerPosition(rating);
                } else {
                    // Jeśli ocena nie istnieje lub jest błąd, ustaw na "Oceń"
                    ratingSpinner.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {
                Log.e("RatingFetchError", "Error fetching rating: ", t);
                ratingSpinner.setSelection(0); // Ustaw na "Oceń" w przypadku błędu
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
                    // Ustaw tekst notatki w EditText
                    noteEditText.setText(noteText);
                } else {
                    // Jeśli notatka nie istnieje lub jest błąd, ustaw puste pole
                    noteEditText.setText("błąd");
                }
            }

            @Override
            public void onFailure(Call<NoteResponse> call, Throwable t) {
                Log.e("NoteFetchError", "Error fetching note: ", t);
                noteEditText.setText(""); // Ustaw puste pole w przypadku błędu
            }
        });
    }


    private void setRatingSpinnerPosition(int rating) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) ratingSpinner.getAdapter();
        int position = adapter.getPosition(String.valueOf(rating));
        ratingSpinner.setSelection(position);
    }

    private void createNote(int recipeId, String noteText) {
        // Tutaj logika tworzenia komentarza za pomocą API
        // Przykład użycia Retrofit do wysłania komentarza
        UserApiService apiService = ApiClient.getUserService();

        String username = SessionManager.getInstance(getContext()).getUsername();

        Note newNote = new Note(recipeId, username, noteText);
        apiService.addNote(newNote).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Notatka została dodany", Toast.LENGTH_SHORT).show();
                    hideKeyboardFrom(getContext(), view);

                    // Możesz odświeżyć listę komentarzy itd.
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
        // Pobranie nazwy użytkownika z menedżera sesji lub innego źródła przechowującego dane użytkownika
        String username = SessionManager.getInstance(getContext()).getUsername();

        // Utworzenie instancji serwisu API
        UserApiService apiService = ApiClient.getUserService();

        // Utworzenie obiektu żądania
        FavouriteToggleRequest request = new FavouriteToggleRequest(username, recipeId);

        // Wywołanie metody API do przełączania ulubionych
        apiService.toggleFavorite(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Informacja dla użytkownika o pomyślnym dodaniu/usunięciu przepisu z ulubionych
                    Toast.makeText(getContext(), "Status ulubionych zmieniony", Toast.LENGTH_SHORT).show();

                    // Tutaj możesz odświeżyć UI, np. zmienić ikonę serca
                } else {
                    // Obsługa odpowiedzi niepomyślnej, np. błędu walidacji lub problemów serwera
                    Toast.makeText(getContext(), "Nie udało się zmienić statusu ulubionych", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Obsługa błędu połączenia
                Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfRecipeIsFavorite(int recipeId) {
        String username = SessionManager.getInstance(getContext()).getUsername();
        // Utworzenie instancji serwisu API
        UserApiService apiService = ApiClient.getUserService();

        // Wywołanie metody checkFavorite z interfejsu API
        Call<CheckFavouriteResponse> call = apiService.checkFavorite(username, recipeId);
        call.enqueue(new Callback<CheckFavouriteResponse>() {
            @Override
            public void onResponse(Call<CheckFavouriteResponse> call, Response<CheckFavouriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obsługa odpowiedzi - sprawdzenie, czy przepis jest ulubiony
                    boolean isFavorite = response.body().isFavourite();
                    if (isFavorite) {
                        // Przepis jest w ulubionych
                        Log.d("CheckFavorite", "Przepis jest dodany do ulubionych.");
                        imageView.setImageResource(R.drawable.heart_full);
                        imageView.setTag("full"); // Ustaw tag, aby śledzić aktualny stan obrazka
                    } else {
                        // Przepis nie jest w ulubionych
                        Log.d("CheckFavorite", "Przepis nie jest dodany do ulubionych.");
                        imageView.setImageResource(R.drawable.heart_empty);
                        imageView.setTag("empty"); // Ustaw tag, aby śledzić aktualny stan obrazka
                    }
                } else {
                    // Błąd podczas komunikacji z serwerem lub błąd po stronie serwera
                    Log.e("CheckFavorite", "Nie udało się sprawdzić ulubionych.");
                }
            }

            @Override
            public void onFailure(Call<CheckFavouriteResponse> call, Throwable t) {
                // Błąd połączenia z serwerem lub inny błąd
                Log.e("CheckFavorite", "Błąd połączenia: " + t.getMessage());
            }
        });
    }









}