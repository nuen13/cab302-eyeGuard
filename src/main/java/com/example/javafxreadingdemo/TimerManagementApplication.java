package com.example.javafxreadingdemo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TimerManagementApplication extends Application {
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int breakInterval = 60; // Default break interval in seconds

    private boolean breakIntervalSet = false; //Flag to track if break interval has been set.

    @FXML
    private Label timerLabel;
    @FXML
    private TextField breakIntervalField;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        VBox root = fxmlLoader.load();
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("eyeGuard App");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void initialize() {
        // Initialize timer label
        updateTimerLabel();

        // Create timeline for the timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            if (secondsElapsed % breakInterval == 0) {
                handleBreak(); // Call method to handle break
            }
            updateTimerLabel();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void handleBreak() {
        //Pause timeline and reset seconds before showing break message.
        timeline.pause();
        secondsElapsed = 0;
        updateTimerLabel();

        // Display an alert with the break message
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Break Time");
            alert.setHeaderText(null);
            alert.setContentText("Ring Ring... It is time for a Break");
            alert.showAndWait();
        });
    }

    private void updateTimerLabel() {
        int hours = secondsElapsed / 3600;
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d:%02d | Break Interval: %s sec", hours, minutes, seconds, breakIntervalField.getText()));
    }

    @FXML
    private void onStartButtonClicked(ActionEvent event) {
        if (breakIntervalSet) {
            timeline.play();
        } else{
            showAlert("Start Error", "Please set the break interval before starting the timer.");

        }
    }

    @FXML
    private void onPauseButtonClicked(ActionEvent event) {
        timeline.pause();
    }

    @FXML
    private void onResetButtonClicked(ActionEvent event) {
        secondsElapsed = 0;
        breakIntervalField.setText(""); // Clear the text field for break interval
        updateTimerLabel();
        timeline.stop();
        breakIntervalSet = false; //Reset the flag as timer is reset
    }

    @FXML
    private void onAnalyticsButtonClicked(ActionEvent event) {
        try {
            // Close the current stage if you want to open analytics in the same window
            // ((Stage)timerLabel.getScene().getWindow()).close();

            // Open the analytics in a new window
            Analytics analytics = new Analytics();
            Stage analyticsStage = new Stage();
            analytics.start(analyticsStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open the analytics page.");
        }
    }

    @FXML
    private void onSetBreakIntervalButtonClicked(ActionEvent event) {
        try {
            int newInterval = Integer.parseInt(breakIntervalField.getText());
            if (newInterval > 0) {
                breakInterval = newInterval;
                updateTimerLabel(); //Update the timer label
                breakIntervalSet = true; // Update flag to indicate interval is set
            } else {
                showAlert("Invalid Input", "Break interval must be greater than zero.");// Handle invalid input (negative or zero)
                breakIntervalSet = false; //Ensure flag is false if invalid input
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");// Handle invalid input (not a number)
            breakIntervalSet = false; //Ensure flag is false if invalid input
        }
    }
    private void showAlert(String title, String content){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}