package com.example.jproject;
import java.io.IOException;
import java.lang.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Optional;

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
    public void addUser2(ActionEvent event) {
        User newUser = captureUserInput();
        insertUserIntoDatabase(newUser);
    }

    private User captureUserInput() {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("New User Input");
        usernameDialog.setHeaderText("Enter data for the new user:");
        usernameDialog.setContentText("Username:");
        Optional<String> usernameResult = usernameDialog.showAndWait();

        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("New User Input");
        emailDialog.setHeaderText("Enter data for the new user:");
        emailDialog.setContentText("Email:");
        Optional<String> emailResult = emailDialog.showAndWait();

        TextInputDialog phoneDialog = new TextInputDialog();
        phoneDialog.setTitle("New User Input");
        phoneDialog.setHeaderText("Enter data for the new user:");
        phoneDialog.setContentText("Phone:");
        Optional<String> phoneResult = phoneDialog.showAndWait();

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("New User Input");
        passwordDialog.setHeaderText("Enter data for the new user:");
        passwordDialog.setContentText("Password:");
        Optional<String> passwordResult = passwordDialog.showAndWait();

        if (usernameResult.isPresent() && emailResult.isPresent() && phoneResult.isPresent() && passwordResult.isPresent()) {
            String username = usernameResult.get();
            String email = emailResult.get();
            String phone = phoneResult.get();
            String password = passwordResult.get();

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setPassword(password);
            return newUser;
        } else {
            return null;
        }
    }

    private void insertUserIntoDatabase(User user) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (username, email, phone, password_hash) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPhone());
                statement.setString(4, user.getPassword());

                int updatedRows = statement.executeUpdate();
                if (updatedRows > 0) {
                    connection.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLoginButtonAction(ActionEvent event) throws IOException {
        String username1 = usernameField.getText();
        String password = passwordField.getText();
        if (authenticate(username1, password)) {
            showAlert("Login Successful", "Welcome, " + username1 + "!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fitnessTracker.fxml"));
                Parent nextRoot = loader.load();
                NextPageController nextPageController = loader.getController();
                ObservableList<Logs> data = nextPageController.getDataFromDatabase(username1);
                nextPageController.setDataInTable(data);
                nextPageController.setUsernameFromLogin(username1);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setTitle("Exercise logs");
                Scene scene = new Scene(nextRoot);
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
