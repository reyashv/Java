package com.example.jproject;

import java.sql.*;
import java.lang.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        Label welcomeLabel = new Label("Hello " + userName);
        welcomeLabel.setStyle("-fx-font-size: 23px;");
        topVBox.getChildren().add(welcomeLabel);
        borderPane.setTop(topVBox);

        // Create a VBox for the exercise logs
        VBox exerciseLogBox = new VBox(10);
        exerciseLogBox.setPadding(new Insets(20, 20, 20, 20));
        exerciseLogBox.setStyle("-fx-background-color: #ffffff;"); // Set background color
        exerciseLogBox.setAlignment(Pos.TOP_LEFT);

        // Add a heading label for exercise logs
        Label exerciseLogsHeading = new Label("Exercise Logs");
        exerciseLogsHeading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        exerciseLogBox.getChildren().add(exerciseLogsHeading);

        StringBuilder exerciseLogText = new StringBuilder();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT el.log_id, el.log_date, el.duration_minutes, el.sets, el.reps, el.notes, " +
                             "ex.exercise_id, ex.exercise_name, ex.description,ex.muscle_group, ex.calorie_burn_per_minute " +
                             "FROM exercise_logs el " +
                             "INNER JOIN exercises ex ON el.exercise_id = ex.exercise_id " +
                             "WHERE el.user_id = ?")) {

            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            Label exerciseLogLabel = new Label();
            while (resultSet.next()) {
                int logId = resultSet.getInt("log_id");
                int exerciseID = resultSet.getInt("exercise_id");
                String exerciseDate = resultSet.getString("log_date");
                int durationMinutes = resultSet.getInt("duration_minutes");
                String exerciseName = resultSet.getString("exercise_name");
                String description = resultSet.getString("description");
                String muscleGroup = resultSet.getString("muscle_group");
                String calorieburn = resultSet.getString("calorie_burn_per_minute");

                exerciseLogText.append("Log ID: ").append(logId).append("\n");
                exerciseLogText.append("Exercise Date: ").append(exerciseDate).append("\n");
                exerciseLogText.append("Duration (minutes): ").append(durationMinutes).append("\n");
                exerciseLogText.append("Exercise Name: ").append(exerciseName).append("\n");
                exerciseLogText.append("Description: ").append(description).append("\n");
                exerciseLogText.append("Muscle Group: ").append(muscleGroup).append("\n");
                exerciseLogText.append("Calories burnt per minute: ").append(calorieburn).append("\n\n");

            }

            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error fetching exercise logs: " + e.getMessage());
        }

        // Create a Label to display exercise logs
        Label exerciseLogLabel = new Label(exerciseLogText.toString());
        exerciseLogLabel.setStyle("-fx-font-size: 16px;");

        // Add the exercise log Label to the exercise log VBox
        exerciseLogBox.getChildren().addAll(exerciseLogLabel);

        // Create a VBox for fitness goals
        VBox fitnessGoalsBox = new VBox(10);
        fitnessGoalsBox.setPadding(new Insets(20, 20, 20, 20));
        fitnessGoalsBox.setStyle("-fx-background-color: #ffffff;"); // Set background color
        fitnessGoalsBox.setAlignment(Pos.TOP_LEFT);

        // Add a heading label for fitness goals
        Label fitnessGoalsHeading = new Label("Fitness Goals");
        fitnessGoalsHeading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        fitnessGoalsBox.getChildren().add(fitnessGoalsHeading);

        StringBuilder fitnessGoalsText = new StringBuilder();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT goal_id, goal_type, target_value, target_date, achieved " +
                             "FROM fitness_goals " +
                             "WHERE user_id = ?")) {

            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int goalId = resultSet.getInt("goal_id");
                String goalType = resultSet.getString("goal_type");
                int TargetValue = resultSet.getInt("target_value");
                String targetDate = resultSet.getString("target_date");
                boolean target = resultSet.getBoolean("achieved");

                fitnessGoalsText.append("Goal ID: ").append(goalId).append("\n");
                fitnessGoalsText.append("Goal Type: ").append(goalType).append("\n");
                fitnessGoalsText.append("Target Value: ").append(TargetValue).append("\n");
                fitnessGoalsText.append("Target Date: ").append(targetDate).append("\n");
                fitnessGoalsText.append("Target: ").append(target).append("\n\n");
            }

            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error fetching fitness goals: " + e.getMessage());
        }

        // Create a Label to display fitness goals
        Label fitnessGoalsLabel = new Label(fitnessGoalsText.toString());
        fitnessGoalsLabel.setStyle("-fx-font-size: 16px;");

        // Add the fitness goals Label to the fitness goals VBox
        fitnessGoalsBox.getChildren().addAll(fitnessGoalsLabel);

        // Create an HBox to hold both exercise logs and fitness goals boxes
        HBox contentBox = new HBox(20);
        contentBox.setPadding(new Insets(20, 20, 20, 20));
        contentBox.setAlignment(Pos.TOP_LEFT);

        // Add exercise logs and fitness goals boxes to the content HBox
        contentBox.getChildren().addAll(exerciseLogBox, fitnessGoalsBox);

        // Set the content HBox in the center of the BorderPane
        borderPane.setCenter(contentBox);

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
