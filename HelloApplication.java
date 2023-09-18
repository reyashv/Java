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
    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "shreya123";
    
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            if (connection != null) {
                System.out.println("Connected to the database!");
                retrieveData();
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Connection failed. Check the error details:");
            e.printStackTrace();
        }
        launch(args);
    }

    private static void retrieveData() {
        String sql = "SELECT * FROM exercises";
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String column1Value = resultSet.getString("exercise_name");
                String column2Value = resultSet.getString("description");
                System.out.println("Column1: " + column1Value + ", Column2: " + column2Value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                // You can navigate to the main fitness tracker screen here
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

    private static Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(jdbcURL, username, password);
    }

    private static boolean authenticate(String username, String password) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT password_hash FROM users WHERE username = ?")) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password_hash");

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
