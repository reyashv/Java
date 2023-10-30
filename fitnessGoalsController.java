package com.example.jproject;
import java.io.*;
import java.lang.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;

public class FitnessGoalsController implements Initializable {
    @FXML
    public TableView<fitness> table;
    @FXML
    public TableColumn<fitness, Integer> GoalID;
    @FXML
    public TableColumn<fitness, String> GoalType;
    @FXML
    public TableColumn<fitness, Double> TargetVal;
    @FXML
    public TableColumn<fitness, String> TargetDate;
    @FXML
    public TableColumn<fitness, Boolean> Achieved;
    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "shreya123";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GoalID.setCellValueFactory(new PropertyValueFactory<>("GoalID"));
        GoalType.setCellValueFactory(new PropertyValueFactory<>("GoalType"));
        TargetVal.setCellValueFactory(new PropertyValueFactory<>("TargetVal"));
        TargetDate.setCellValueFactory(new PropertyValueFactory<>("TargetDate"));
        Achieved.setCellValueFactory(new PropertyValueFactory<>("Achieved"));
        //ObservableList<fitness> data = getDataFromDatabase1(username1);
        //table.setItems(data);
    }
    ObservableList<fitness> getDataFromDatabase1(String username1) {
        ObservableList<fitness> data = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT goal_id, goal_type, target_value, target_date, achieved FROM fitness_goals el INNER JOIN users u ON el.user_id = u.user_id WHERE u.username = ?")) {
            statement.setString(1, username1);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                fitness fit = new fitness();
                fit.setGoalID(resultSet.getInt("goal_id"));
                fit.setGoalType(resultSet.getString("goal_type"));
                fit.setTargetVal(resultSet.getDouble("target_value"));
                fit.setTargetDate(resultSet.getString("target_date"));
                fit.setAchieved(resultSet.getBoolean("achieved"));
                data.add(fit);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setDataInTable(ObservableList<fitness> data) {
        table.setItems(data);
    }
}
