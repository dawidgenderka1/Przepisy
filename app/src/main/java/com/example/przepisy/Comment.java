package com.example.przepisy;

public class Comment {

    private int RecipeID;

    private String Username;
    private String CommentText;
    private String CommentDate; // Format daty jako String, można dostosować typ zależnie od potrzeb

    // Konstruktor
    public Comment(int recipeID, String commentText, String commentDate, String Username) {
        this.RecipeID = recipeID;
        this.CommentText = commentText;
        this.CommentDate = commentDate;
        this.Username = Username;
    }

    // Gettery i settery
    public int getRecipeID() {
        return RecipeID;
    }

    public void setRecipeID(int userID) {
        RecipeID = userID;
    }

    public String getCommentText() {
        return CommentText;
    }

    public void setCommentText(String commentText) {
        CommentText = commentText;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getCommentDate() {
        return CommentDate;
    }

    public void setCommentDate(String commentDate) {
        CommentDate = commentDate;
    }

    // Można dodać dodatkowe metody/formatowanie daty itp. w zależności od potrzeb
}
