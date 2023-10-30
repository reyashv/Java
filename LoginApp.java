package com.example.jproject;
import java.sql.*;
import java.lang.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class LoginApp extends Application {
    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "shreya123";
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(new Scene(root, 700, 700));
        primaryStage.show();
    }

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
}
