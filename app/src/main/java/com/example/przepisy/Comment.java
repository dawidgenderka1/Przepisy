package com.example.przepisy;

public class Comment {

    private int RecipeID;

    private String Username;
    private String CommentText;
    private String CommentDate;

    public Comment(int recipeID, String commentText, String commentDate, String Username) {
        this.RecipeID = recipeID;
        this.CommentText = commentText;
        this.CommentDate = commentDate;
        this.Username = Username;
    }

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
}
