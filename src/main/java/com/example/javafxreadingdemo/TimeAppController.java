package com.example.javafxreadingdemo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class TimeAppController {

    private Timeline timeline;
    private int secondsElapsed = 0;
    private int timeInterval = 60; // Default break interval in seconds
    private boolean timerSet = false; //Flag to track if break interval has been set.
    private boolean startActive = false;
    private boolean onstartBtn = true;
    private int timeInMinute = 0;
    @FXML
    private Label timerLabel;
    @FXML
    private TextField timeIntervalField;
    @FXML
    private AnchorPane rootPane;
    private int newTime = 0;
    @FXML
    private Button startBtn;
    @FXML
    private Button lowBtn;
    @FXML
    private Button midBtn;
    @FXML
    private Button highBtn;

    private boolean timerRun = false;

    private boolean breakTimePreset = false;

    // initialize timer
    @FXML
    private void initialize() {

        setBackgroundTheme ();
        // Initialize timer label
        newTime = 1;
        updateTimerTime(newTime);

        // Create timeline for the timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed--;
            if (secondsElapsed % timeInterval == 0) {
                // Call method to handle break
                timerEnd();
            }
            updateTimerLabel();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private String alertText = "";
    private void timerEnd(){
        if(breakTimePreset){
            alertText = "AAAAA .... Get Back To Workkkk";
            playSound(ShareVarSetting.alertSound);
            breakTimePreset = false;
            newTime = 2;
        }
        else {
            alertText = "Ring Ring... It is time for a Break";
            playSound(ShareVarSetting.alertSound);
            breakTimePreset = true;
            newTime = 4;
        }
        handleBreak();
        changeTimePreset(breakTimePreset);
        updateTimerTime(newTime);
    }

    // pause timer and send alert box
    private void handleBreak() {
//        //Pause timeline and reset seconds before showing break message.
//        timeline.pause();
//        secondsElapsed = 0;
//        updateTimerLabel();

        // Display an alert with the break message
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Break Time");
            alert.setHeaderText(null);
            alert.setContentText(alertText);
            alert.showAndWait();
        });


    }

    // get custom time -> change to correct format
    private void updateTimerLabel() {

        int hours;
        int min;
        int sec;

        hours = secondsElapsed / 3600;
        min = (secondsElapsed % 3600) / 60;
        sec = secondsElapsed % 60;

        timerLabel.setText(String.format("%02d:%02d:%02d", hours, min, sec));

    }
    private void updateTimerTime(int newTime){
        if(timerRun){
            timeline.stop();
            timerRun = false;
        }
        secondsElapsed = timeInMinute = newTime;
        timerSet = true;

        updateTimerLabel();
        updateStartBtn(timerRun);
    }
    private void updateStartBtn(boolean timerRun){
        if (timerRun){
            startBtn.setText("Pause");
        }
        else {
            startBtn.setText("Start");
        }
    };

    // start timer
    @FXML
    private void StartTime(ActionEvent event) {
        if (!timerRun) {
            if (timerSet) {
                timerRun = true;
                startActive = true;


                timeline.play();

                updateStartBtn(timerRun);

            } else {
                showAlert("Start Error", "Please set the break interval before starting the timer.");
            }
        }
        else{
            timerRun = false;
            timeline.pause();

            updateStartBtn(timerRun);
        }
        System.out.println(onstartBtn);
    }

    private void changeTimePreset(boolean breakTimePreset){
        if(breakTimePreset){
            lowBtn.setText("Short break (10 mins)");
            midBtn.setText("Long break (25 mins)");
            highBtn.setText("Nappie (1 hour)");
        }
        else {
            lowBtn.setText("Bursts (30 mins)");
            midBtn.setText("Grind (45 mins)");
            highBtn.setText("Cram (1 hour)");

        }
    };

    @FXML
    private void onbreakTime(ActionEvent event){
        breakTimePreset = true;
        newTime = 600;

        updateTimerTime(newTime);
        changeTimePreset(breakTimePreset);

    };

    @FXML
    private void onworkTime(ActionEvent event){
        breakTimePreset = false;
        newTime = 1;

        updateTimerTime(newTime);
        changeTimePreset(breakTimePreset);
    };


//    // reset timer
//    @FXML
//    private void onResetButtonClicked(ActionEvent event) {
//        secondsElapsed = 0;
//        timeInMinute = 0;
//        timeIntervalField.setText(""); // Clear the text field for break interval
//        startActive = false;
//        updateTimerLabel();
//        timeline.stop();
//        timerSet = false; //Reset the flag as timer is reset
//    }


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

    // custom Time
    // get time
//    @FXML
//    private void onSettimeIntervalButtonClicked(ActionEvent event) {
//        try {
//            int newInterval = Integer.parseInt(timeIntervalField.getText());
//            if (newInterval > 0) {
//                timeInterval = newInterval;
//                timeInMinute = Integer.parseInt(timeIntervalField.getText());
//                updateTimerLabel(); //Update the timer label
//                timerSet = true; // Update flag to indicate interval is set
//            } else {
//                showAlert("Invalid Input", "Break interval must be greater than zero.");// Handle invalid input (negative or zero)
//                timerSet = false; //Ensure flag is false if invalid input
//            }
//        } catch (NumberFormatException e) {
//            showAlert("Invalid Input", "Please enter a valid number.");// Handle invalid input (not a number)
//            timerSet = false; //Ensure flag is false if invalid input
//        }
//    }


    @FXML
    private void onLowClicked(ActionEvent event){
        if (!breakTimePreset){
            newTime= 1;
        } else {
            newTime= 600;
        }
        updateTimerTime(newTime);
    }
    //set up alert

    @FXML
    private void onMidClicked(ActionEvent event){
        if (!breakTimePreset){
            newTime= 2700;
        } else {
            newTime= 1500;
        }
        updateTimerTime(newTime);
    }
    //set up alert

    @FXML
    private void onHighClicked(ActionEvent event){
        if (!breakTimePreset){
            newTime= 3600;
        } else {
            newTime= 3600;
        }
        updateTimerTime(newTime);
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

        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    private void setBackgroundTheme (){
        BackgroundFill backgroundFill = new BackgroundFill(ShareVarSetting.themeColor, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        rootPane.setBackground(background);
    }

    private Clip clip;
    private void playSound(URL soundURL) {
        if (soundURL != null) {
            try {
                // Stop the previous sound if it's playing
                if (clip != null) {
                    clip.stop();
                    clip.close();
                }

                // Load the audio file
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);

                // Get a sound clip resource
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // Play the audio clip
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
                System.err.println("Error playing sound file: " + soundURL);
            }
        }
    }
}

