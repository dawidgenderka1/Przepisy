package com.example.przepisy;

public class User {
    private String username;
    private String email;
    private String password;

    // Konstruktor
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }



    // Getter dla username
    public String getUsername() {
        return username;
    }

    // Setter dla username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter dla email
    public String getEmail() {
        return email;
    }

    // Setter dla email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter dla password
    public String getPassword() {
        return password;
    }

    // Setter dla password
    public void setPassword(String password) {
        this.password = password;
    }
}
