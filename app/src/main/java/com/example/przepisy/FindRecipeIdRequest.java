package com.example.przepisy;

public class FindRecipeIdRequest {
    private String username;
    private String title;
    private String description;

    public FindRecipeIdRequest(String username, String title, String description) {
        this.username = username;
        this.title = title;
        this.description = description;

    }
}
