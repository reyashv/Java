package com.example.jproject;

import java.sql.*;
import java.lang.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class HelloApplication extends Application {
    static int userID;
    static String userName;
    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "shreya123";
    
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            if (connection != null) {
                System.out.println("Connected to the database!");
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Connection failed. Check the error details:");
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fitness Tracker Login");
        BorderPane borderPane = new BorderPane();
        VBox topVBox = new VBox(10);
        topVBox.setPadding(new Insets(20, 20, 20, 20));
        topVBox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to Your Fitness Tracker Application");
        welcomeLabel.setStyle("-fx-font-size: 23px;");

        topVBox.getChildren().add(welcomeLabel);
        topVBox.setStyle("-fx-background-color: #d0bdf4;");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #a28089;");
        Label loginPageLabel = new Label("Login:");
        GridPane.setConstraints(loginPageLabel, 0, 0, 2, 1);
        loginPageLabel.setStyle("-fx-font-size: 23px;");

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 1);
        usernameLabel.setStyle("-fx-font-size: 16px;");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter your username");
        usernameInput.setStyle("-fx-font-size: 15px;");
        GridPane.setConstraints(usernameInput, 1, 1);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 2);
        passwordLabel.setStyle("-fx-font-size: 16px;");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter your password");
        passwordInput.setStyle("-fx-font-size: 15px;");
        GridPane.setConstraints(passwordInput, 1, 2);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 3);
        passwordLabel.setStyle("-fx-font-size: 16px;");

        loginButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();

            if (authenticate(username, password)) {
                showAlert("Login Successful", "Welcome, " + username + "!");
                showNewPage();
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        });

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton,loginPageLabel);

        borderPane.setTop(topVBox);
        borderPane.setCenter(grid);

        Scene scene = new Scene(borderPane);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setWidth(bounds.getWidth() * 0.8);
        primaryStage.setHeight(bounds.getHeight() * 0.8);

        primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) / 2);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void showNewPage() {
        Stage newStage = new Stage();
        newStage.setTitle("Fitness Tracker Page");
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #d0bdf4;");
        VBox topVBox = new VBox(10);
        topVBox.setPadding(new Insets(20, 20, 20, 20));
        topVBox.setAlignment(Pos.TOP_LEFT);
        Label welcomeLabel = new Label("Hello "+userName);
        welcomeLabel.setStyle("-fx-font-size: 23px;");
        topVBox.getChildren().add(welcomeLabel);
        borderPane.setTop(topVBox);
        StringBuilder exerciseLogText = new StringBuilder();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT log_id, exercise_id, log_date, duration_minutes, sets, reps, notes " +
                             "FROM exercise_logs " +
                             "WHERE user_id = ?")) {

            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            Label loginPageLabel = new Label("Exercise Logs :");
            loginPageLabel.setStyle("-fx-font-size: 23px;");
            while (resultSet.next()) {
                int logId = resultSet.getInt("log_id");
                int exerciseID = resultSet.getInt("exercise_id");
                String exerciseDate = resultSet.getString("log_date");
                int durationMinutes = resultSet.getInt("duration_minutes");

                exerciseLogText.append("Log ID: ").append(logId).append("\n");
                exerciseLogText.append("Exercise ID: ").append(exerciseID).append("\n");
                exerciseLogText.append("Exercise Date: ").append(exerciseDate).append("\n");
                exerciseLogText.append("Duration (minutes): ").append(durationMinutes).append("\n\n");
            }

            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error fetching exercise logs: " + e.getMessage());
        }
        Label exerciseLogLabel = new Label(exerciseLogText.toString());
        exerciseLogLabel.setStyle("-fx-font-size: 16px;");
        borderPane.setLeft(exerciseLogLabel);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        newStage.setWidth(bounds.getWidth() * 0.9);
        newStage.setHeight(bounds.getHeight() * 0.9);

        newStage.setX((bounds.getWidth() - newStage.getWidth()) / 2);
        newStage.setY((bounds.getHeight() - newStage.getHeight()) / 2);

        Scene blankScene = new Scene(borderPane);
        newStage.setScene(blankScene);
        newStage.show();
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
                userID = resultSet.getInt("user_id");
                userName= resultSet.getString("username");
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
