package com.example.przepisy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String url = "jdbc:mysql://34.159.25.111:3306/RecipeDatabase?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "przepisy";

    public static Connection getConnection() {
        Connection con = null;
        try {
            // Zaktualizowane do nowej nazwy klasy sterownika JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void addUser(String username, String email, String password) {
        String query = "INSERT INTO Users (Username, Email, Password) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, password);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Możesz chcieć tutaj dodać bardziej zaawansowaną obsługę błędów
        }
    }

    /*public static boolean loginUser(String username, String password) {
        boolean loginSuccess = false;
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                // Jeśli ResultSet nie jest pusty, użytkownik istnieje, więc logowanie jest udane
                loginSuccess = rs.next();
            } catch (SQLException e) {
                System.out.println("Błąd podczas wykonania zapytania: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas nawiązywania połączenia: " + e.getMessage());
        }
        return loginSuccess;
    }*/

}
