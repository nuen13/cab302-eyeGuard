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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;

public class TimerManagementApplication extends Application {
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int breakInterval = 60; // Default break interval in seconds
    private int userId; // User ID of the logged-in user

    private boolean breakIntervalSet = false; // Flag to track if break interval has been set.
    private UserDAO userDAO;

    @FXML
    private Label timerLabel;
    @FXML
    private TextField breakIntervalField;

    public TimerManagementApplication(int userId) {
        this.userId = userId;
    }

    // Default constructor for Application launch
    public TimerManagementApplication() {
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        VBox root = fxmlLoader.load();
        TimerManagementApplication controller = fxmlLoader.getController();
        controller.setUserId(this.userId); // Ensure userId is set in the controller
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("eyeGuard App");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void initialize() {
        userDAO = new UserDAO();
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
        // Pause timeline and reset seconds before showing break message.
        timeline.pause();
        saveFocusSession(); // Save the session when the break is handled
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
        } else {
            showAlert("Start Error", "Please set the break interval before starting the timer.");
        }
    }

    @FXML
    private void onPauseButtonClicked(ActionEvent event) {
        timeline.pause();
        saveFocusSession(); // Save the session when the timer is paused
    }

    @FXML
    private void onResetButtonClicked(ActionEvent event) {
        saveFocusSession(); // Save the session when the timer is reset
        secondsElapsed = 0;
        breakIntervalField.setText(""); // Clear the text field for break interval
        updateTimerLabel();
        timeline.stop();
        breakIntervalSet = false; // Reset the flag as timer is reset
    }

    @FXML
    private void onAnalyticsButtonClicked(ActionEvent event) {
        try {
            // Close the current stage if you want to open analytics in the same window
            // ((Stage)timerLabel.getScene().getWindow()).close();

            // Open the analytics in a new window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Analytics analytics = new Analytics();
            analytics.setUserId(userId); // Pass the userId to Analytics
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
                updateTimerLabel(); // Update the timer label
                breakIntervalSet = true; // Update flag to indicate interval is set
            } else {
                showAlert("Invalid Input", "Break interval must be greater than zero."); // Handle invalid input (negative or zero)
                breakIntervalSet = false; // Ensure flag is false if invalid input
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number."); // Handle invalid input (not a number)
            breakIntervalSet = false; // Ensure flag is false if invalid input
        }
    }

    private void saveFocusSession() {
        if (secondsElapsed > 0 && userId > 0) {
            userDAO.insertFocusSession(userId, LocalDate.now(), secondsElapsed);
            secondsElapsed = 0;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private Button setting;

    @FXML
    protected void gotosetting() throws IOException {
        Stage stage = (Stage) this.setting.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("setting-view.fxml"));
        Scene scene = new Scene((Parent) fxmlLoader.load(), 640.0, 360.0);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
