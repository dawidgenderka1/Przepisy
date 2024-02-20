package com.example.przepisy.api;

import com.example.przepisy.Comment;
import com.example.przepisy.Recipe;
import com.example.przepisy.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

}

