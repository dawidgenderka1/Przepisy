package com.example.przepisy.api;

import com.example.przepisy.CheckFavouriteResponse;
import com.example.przepisy.Comment;
import com.example.przepisy.FavouriteToggleRequest;
import com.example.przepisy.Note;
import com.example.przepisy.NoteResponse;
import com.example.przepisy.Rating;
import com.example.przepisy.RatingResponse;
import com.example.przepisy.Recipe;
import com.example.przepisy.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    @POST("addUser")
    Call<Void> addUser(@Body User user);

    @POST("login")
    Call<Void> loginUser(@Body User user);

    @GET("/getRecipes")
    Call<List<Recipe>> getRecipes();

    @POST("addRecipe")
    Call<Void> addRecipe(@Body Recipe recipe);

    @GET("/getComments/{recipeId}")
    Call<List<Comment>> getComments(@Path("recipeId") int recipeId);

    @POST("addComment")
    Call<Void> createComment(@Body Comment comment);

    @POST("/addRating")
    Call<Void> addRating(@Body Rating rating);

    @GET("/getRating")
    Call<RatingResponse> getRating(@Query("RecipeID") int RecipeID, @Query("Username") String Username);

    @POST("/addNote")
    Call<Void> addNote(@Body Note note);

    @GET("/getNotes")
    Call<NoteResponse> getNote(@Query("RecipeID") int RecipeID, @Query("Username") String Username);

    @POST("toggleFavorite")
    Call<Void> toggleFavorite(@Body FavouriteToggleRequest favoriteToggleRequest);

    @GET("checkFavorite")
    Call<CheckFavouriteResponse> checkFavorite(@Query("username") String username, @Query("recipeId") int recipeId);







}

