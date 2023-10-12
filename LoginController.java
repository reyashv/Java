package com.example.jproject;
import java.lang.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "shreya123";

    public void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println(username+password);

        if (authenticate(username, password)) {
            showAlert("Login Successful", "Welcome, " + username + "!");
        } else {
            // Invalid login, show an error message.
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private static Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(jdbcURL, username, password);
    }

    private static boolean authenticate(String username, String password) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT username,user_id, password_hash FROM users WHERE username = ?")) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password_hash");
                int userID = resultSet.getInt("user_id");
                String userName= resultSet.getString("username");
                if (storedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
