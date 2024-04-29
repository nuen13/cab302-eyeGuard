package com.example.javafxreadingdemo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class TimerManagementApplication extends Application {
    private Timeline timeline;
    private int secondsElapsed = 0;

    @FXML
    private Label timerLabel;

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
            updateTimerLabel();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void updateTimerLabel() {
        int hours = secondsElapsed / 3600;
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    @FXML
    private void onStartButtonClicked(ActionEvent event) {
        timeline.play();
    }

    @FXML
    private void onPauseButtonClicked(ActionEvent event) {
        timeline.pause();
    }

    @FXML
    private void onResetButtonClicked(ActionEvent event) {
        secondsElapsed = 0;
        updateTimerLabel();
        timeline.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
