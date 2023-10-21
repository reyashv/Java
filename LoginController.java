package com.example.jproject;
import java.io.IOException;
import java.lang.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class LoginController{
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "shreya123";
    String username1;
    public void handleLoginButtonAction(ActionEvent event) throws IOException {
        username1 = usernameField.getText();
        String password = passwordField.getText();
        if (authenticate(username1, password)) {
            showAlert("Login Successful", "Welcome, " + username1 + "!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fitnessTracker.fxml"));
                Parent nextRoot = loader.load();
                NextPageController nextPageController = loader.getController();
                ObservableList<Logs> data = nextPageController.getDataFromDatabase(username1);
                nextPageController.setDataInTable(data);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(nextRoot,700,700);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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
