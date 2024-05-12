package com.example.javafxreadingdemo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;

public class newMain {

    private Timeline timeline;
    private int secondsElapsed = 0;
    private int breakInterval = 60; // Default break interval in seconds
    private boolean breakIntervalSet = false; //Flag to track if break interval has been set.
    private boolean startActive = false;
    private boolean onstartBtn = true;
    private int timeInMinute = 0;
    @FXML
    private Label timerLabel;
    @FXML
    private TextField breakIntervalField;
    @FXML
    private Button burst;

    @FXML
    private Button startBtn;

    // initialize timer
    @FXML
    private void initialize() {
        // Initialize timer label
        updateTimerLabel();

        // Create timeline for the timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed--;
            if (secondsElapsed % breakInterval == 0) {
                handleBreak(); // Call method to handle break
            }
            updateTimerLabel();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    // pause timer and send alert box
    private void handleBreak() {
        //Pause timeline and reset seconds before showing break message.
        timeline.pause();
        secondsElapsed = 0;
        updateTimerLabel();

        // Display an alert with the break message
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Break Time");
            alert.setHeaderText(null);
            alert.setContentText("Ring Ring... It is time for a Break");
            alert.showAndWait();
        });
    }

    // get custom time -> change to correct format
    private void updateTimerLabel() {

        int hours;
        int min;
        int sec;

        int displayTime = timeInMinute / 60;

        if (startActive){
            hours = secondsElapsed / 3600;
            min = (secondsElapsed % 3600) / 60;
            sec = secondsElapsed % 60;

            timerLabel.setText(String.format("%02d:%02d:%02d", hours, min, sec, displayTime));
        }else {
            hours = timeInMinute / 3600;
            min = (timeInMinute % 3600) / 60;
            sec = timeInMinute % 60;
            //timerLabel.setText(String.format("Time: %02d:%02d:%02d | Break Interval: %s sec", hours, minutes, seconds, breakIntervalField.getText()));
            timerLabel.setText(String.format("%02d:%02d:%02d", hours, min, sec, displayTime));
        }
    }

    // start timer
    @FXML
    private void onStartButtonClicked(ActionEvent event) {
        if (onstartBtn) {
            if (breakIntervalSet) {
                onstartBtn = false;
                startActive = true;
                secondsElapsed = timeInMinute;
                timeline.play();
                startBtn.setText("Pause");

            } else {
                showAlert("Start Error", "Please set the break interval before starting the timer.");
            }
        }
        else{
            onstartBtn = true;
            startBtn.setText("Start");
            timeline.pause();
        }
        System.out.println(onstartBtn);
    }


    // reset timer
    @FXML
    private void onResetButtonClicked(ActionEvent event) {
        secondsElapsed = 0;
        timeInMinute = 0;
        breakIntervalField.setText(""); // Clear the text field for break interval
        startActive = false;
        updateTimerLabel();
        timeline.stop();
        breakIntervalSet = false; //Reset the flag as timer is reset
    }


    // go to analytic page
    @FXML
    private void onAnalyticsButtonClicked(ActionEvent event) {
        try {
            // Close the current stage if you want to open analytics in the same window
            // ((Stage)timerLabel.getScene().getWindow()).close();

            // Open the analytics in a new window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Analytics analytics = new Analytics();
            Stage analyticsStage = new Stage();
            analytics.start(analyticsStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open the analytics page.");
        }
    }

    // get time
    @FXML
    private void onSetBreakIntervalButtonClicked(ActionEvent event) {
        try {
            int newInterval = Integer.parseInt(breakIntervalField.getText());
            if (newInterval > 0) {
                breakInterval = newInterval;
                timeInMinute = Integer.parseInt(breakIntervalField.getText());
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

    @FXML
    private void onBurstClicked(ActionEvent event){
        if (!breakIntervalSet){
            timeInMinute = 1800;
            breakInterval = 1800;

            updateTimerLabel();
            breakIntervalSet = true;
        }else{
            breakIntervalSet = false;

            timeInMinute = 0;
            breakInterval = 0;


        }
    }
    //set up alert

    @FXML
    private void onGrindClicked(ActionEvent event){
        if (secondsElapsed != 0 && breakInterval != 0) {
            System.out.println("boobs 2");
            timeInMinute = 0;
            breakInterval = 0;
        }
        timeInMinute = 3600;
        breakInterval = 3600;

        updateTimerLabel(); //Update the timer label
        breakIntervalSet = true; // Update flag to indicate interval is set
    }
    //set up alert

    @FXML
    private void onCramClicked(ActionEvent event){
        if (!breakIntervalSet) {
            System.out.println("boobs 1");
            timeInMinute = 0;
            breakInterval = 0;
        }

        timeInMinute = 5400;
        breakInterval = 5400;

        updateTimerLabel(); //Update the timer label
        breakIntervalSet = true; // Update flag to indicate interval is set
    }
    //set up alert


    private void showAlert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // go to setting
    @FXML
    private Button setting;
    @FXML
    protected void gotosetting() throws IOException {
        Stage stage = (Stage)this.setting.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("setting-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 640.0, 360.0);
        stage.setScene(scene);
    }
}

