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
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.net.URL;
import java.util.List;


public class TimeAppController {
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int timeInterval = 60; // Default break interval in seconds
    private boolean timerSet = false; // Flag to track if break interval has been set
    private boolean startActive = false;
    private boolean onstartBtn = true;
    private int timeInMinute = 0;
    private int newTime = 0;
    private boolean timerRun = false;
    private boolean breakTimePreset = false;
    private UserDAO userDAO;
    private int userId;

    @FXML
    private Label timerLabel;
    @FXML
    private TextField timeIntervalField;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button startBtn;
    @FXML
    private Button analytics;
    @FXML
    private Button lowBtn;
    @FXML
    private Button worktime;
    @FXML
    private Button breaktime;
    @FXML
    private Button midBtn;
    @FXML
    private Button highBtn;

    @FXML
    private Button customBtn;

    private CustomSettingDAO customSettingDAO;

    private int storedWorkTimePreset = 5; // Default work time preset in seconds (1 hour)
    private int storedBreakTimePreset = 5; // Default break time preset in seconds (10 minutes)



    // Default constructor for Application launch
    public TimeAppController() {

    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @FXML
    private void initialize() {
        customSettingDAO = new CustomSettingDAO();
        customSettingDAO.createCustomSettingTable();

        userDAO = new UserDAO();

        addLogoToLayout();

        // Initialize timer label
        newTime = 1;
        updateTimerTime(newTime);

        worktime.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-cursor: hand;");
        breaktime.setStyle("");

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

        AnchorPane.setTopAnchor(startBtn, 320.0);
        AnchorPane.setTopAnchor(lowBtn, 150.0);
        AnchorPane.setTopAnchor(midBtn, 150.0);
        AnchorPane.setTopAnchor(highBtn, 150.0);
        AnchorPane.setTopAnchor(customBtn, 150.0);
        AnchorPane.setTopAnchor(setting, 5.0);
        AnchorPane.setTopAnchor(timerLabel, 200.0);
        AnchorPane.setRightAnchor(setting, 5.0);
        AnchorPane.setTopAnchor(analytics, 40.0);
        AnchorPane.setRightAnchor(analytics, 5.0);
        AnchorPane.setTopAnchor(worktime, 110.0);
        AnchorPane.setRightAnchor(worktime, 330.0);
        AnchorPane.setTopAnchor(breaktime, 110.0);
        AnchorPane.setRightAnchor(breaktime, 193.0);
        AnchorPane.setRightAnchor(startBtn, 280.0);

        startBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-cursor: hand;");
        lowBtn.setStyle("-fx-background-color: #212121; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-cursor: hand;");
        midBtn.setStyle("-fx-background-color: #212121; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-cursor: hand;");
        highBtn.setStyle("-fx-background-color: #212121; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-cursor: hand;");
        customBtn.setStyle("-fx-background-color: #212121; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-cursor: hand;");

        System.out.println("this is important " + userId);
        loadCustomSetting(userId);
    }

    private ImageView createLogoImageView() {
        Image image = new Image(getClass().getResourceAsStream("/Logo/Logo.jpg.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);  // Set desired width
        imageView.setFitHeight(100); // Set desired height
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void addLogoToLayout() {
        ImageView logoView = createLogoImageView();
        AnchorPane.setTopAnchor(logoView, 10.0);  // Positioning from the top of the AnchorPane
        AnchorPane.setLeftAnchor(logoView, 205.0);  // Align left edge to the AnchorPane's left edge
        AnchorPane.setRightAnchor(logoView, 0.0); // Align right edge to the AnchorPane's right edge
        rootPane.getChildren().add(logoView);
    }

    private void saveFocusSession() {
        if (secondsElapsed > 0 && userId > 0) {
            userDAO.insertFocusSession(userId, LocalDate.now(), secondsElapsed);
            secondsElapsed = 0;
        }
    }

    private String alertText = "";
    private void timerEnd() {
        if (breakTimePreset) {
            // Break time has ended, switch to work time
            alertText = "AAAAA .... Get Back To Workkkk";
            if (ShareVarSetting.alertSound != null) {
                playSound(ShareVarSetting.alertSound, -9.0f); // Reduce volume by 9 decibels
            }
            breakTimePreset = false;
            newTime = storedWorkTimePreset; // Set the new time with the stored work time preset

            // Save the focus session for work time
            saveFocusSession();

        } else {
            // Work time has ended, switch to break time
            alertText = "Ring Ring... It is time for a Break";
            if (ShareVarSetting.alertSound != null) {
                playSound(ShareVarSetting.alertSound, -9.0f); // Reduce volume by 9 decibels
            }
            breakTimePreset = true;
            newTime = storedBreakTimePreset; // Set the new time with the stored break time preset

            // Save the focus session for break time
            saveFocusSession();
        }

        // Handle the break and update UI
        handleBreak();
        changeTimePreset(breakTimePreset);
        updateTimerTime(newTime); // Update the timer with the new time

        StartTime(null);
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
    }

    private void changeTimePreset(boolean breakTimePreset){
        if(breakTimePreset){
            lowBtn.setText("Short (10 mins)");
            midBtn.setText("Long (25 mins)");
            highBtn.setText("Nappie (1 hour)");
            breaktime.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-cursor: hand;");
            worktime.setStyle("");

            if (displayCusTime_break != 0) {
                customBtn.setText(Integer.toString(displayCusTime_break) + " mins");
            }else {
                customBtn.setText("Custom");
            }

        }
        else {
            lowBtn.setText("Bursts (30 mins)");
            midBtn.setText("Grind (45 mins)");
            highBtn.setText("Cram (1 hour)");
            worktime.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-cursor: hand;");
            breaktime.setStyle("");

            if (displayCusTime_work != 0) {
                customBtn.setText(Integer.toString(displayCusTime_work) + " mins");
            }else {
                customBtn.setText("Custom");
            }

        }

    };

    @FXML
    private void onbreakTime(ActionEvent event){
        breakTimePreset = true;
        newTime = storedBreakTimePreset; // Update newTime with the stored break time preset

        updateTimerTime(newTime);
        changeTimePreset(breakTimePreset);
    };

    @FXML
    private void onworkTime(ActionEvent event){
        breakTimePreset = false;
        newTime = storedWorkTimePreset; // Update newTime with the stored work time preset

        updateTimerTime(newTime);
        changeTimePreset(breakTimePreset);
    };



    // go to analytic page
    @FXML
    private void onAnalyticsButtonClicked(ActionEvent event) {
        try {
            if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
            }
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Analytics analytics = new Analytics();
            analytics.setUserId(userId);
            Stage analyticsStage = new Stage();
            analytics.start(analyticsStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open the analytics page.");
        }
    }

    @FXML
    private void onLowClicked(ActionEvent event){
        if (!breakTimePreset){
            storedWorkTimePreset = 1800; // Set the stored work time preset to 30 minutes (1800 seconds)
            newTime= storedWorkTimePreset;
        } else {
            storedBreakTimePreset = 600; // Set the stored break time preset to 10 minutes (600 seconds)
            newTime= storedBreakTimePreset;
        }
        updateTimerTime(newTime);
    }
    //set up alert

    @FXML
    private void onMidClicked(ActionEvent event){
        if (!breakTimePreset){
            storedWorkTimePreset = 2700; // Set the stored work time preset to 45 minutes (2700 seconds)
            newTime= storedWorkTimePreset;
        } else {
            storedBreakTimePreset = 1500; // Set the stored break time preset to 25 minutes (1500 seconds)
            newTime= storedBreakTimePreset;
        }
        updateTimerTime(newTime);
    }
    //set up alert

    @FXML
    private void onHighClicked(ActionEvent event){
        if (!breakTimePreset){
            storedWorkTimePreset = 3600; // Set the stored work time preset to 1 hour (3600 seconds)
            newTime= storedWorkTimePreset;
        } else {
            storedBreakTimePreset = 3600; // Set the stored break time preset to 1 hour (3600 seconds)
            newTime= storedBreakTimePreset;
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
        // Stop the timer if it's running
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }

        Stage stage = (Stage) this.setting.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("setting-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Get the controller and set the userId
        SettingController setController = fxmlLoader.getController();
        setController.setUserId(userId);

        scene.getStylesheets().add(TimerManagementApplication.class.getResource("/SettingStyle.css").toExternalForm());
        stage.setScene(scene);

        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }


    public static Color color;

    SettingController settingController = new SettingController();
    // Get Custom Setting
    public void loadCustomSetting(int userId) {
        List<CustomSetting> customSettings = customSettingDAO.getCustomSetting(userId);
        for (CustomSetting setting : customSettings) {
            updateBackgroundColor(setting.getThemeColor());
            getSound(setting.getSoundAlert()); // Only set the URL, do not play the sound
            getCustomTime(setting.getBreakTime(), setting.getWorkTime());

            System.out.println("Theme Color: " + setting.getThemeColor());
            System.out.println("Sound Alert: " + setting.getSoundAlert());
            System.out.println("Break Time: " + setting.getBreakTime());
            System.out.println("Work Time: " + setting.getWorkTime());
        }
    }

    private void setBackgroundTheme (Color color){
        BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        rootPane.setBackground(background);
    }

    public void updateBackgroundColor(String colorName) {
        System.out.println("aaaa " + colorName);
        if (colorName != null) {
            switch (colorName) {
                case "Default":
                    ShareVarSetting.themeColor  = Color.rgb(0,9,19);
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
                case "Summer":
                    ShareVarSetting.themeColor  = Color.LIGHTCORAL;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
                case "Autumn":
                    ShareVarSetting.themeColor  = Color.LIGHTGOLDENRODYELLOW;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
                case "Winter":
                    ShareVarSetting.themeColor  = Color.LIGHTBLUE;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
                case "Spring":
                    ShareVarSetting.themeColor  = Color.LIGHTGREEN;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
            }
        }
    }

    private Clip clip;
    private void getSound(String soundName) {
        System.out.println("Setting sound: " + soundName); // Debug statement
        if ("Default".equals(soundName)) {
            // Stop the previous sound if it's playing and clear the alert sound
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            ShareVarSetting.soundName = "Default"; // Explicitly set to "Default"
            ShareVarSetting.alertSound = null; // Clear the alert sound
            System.out.println("Sound set to default (no sound)."); // Debug statement
            return;
        }

        ShareVarSetting.soundName = soundName;
        ShareVarSetting.alertSound = getClass().getResource("/soundEffect/" + soundName + ".wav");

        if (ShareVarSetting.alertSound == null) {
            System.err.println("Error: Sound file not found for " + soundName);
        } else {
            System.out.println("Sound set to: " + soundName); // Debug statement
        }
    }
    private void playSound(URL soundURL, float volumeReductionInDecibels) {
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

                // Get the volume control
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Set the volume (reduce the volume)
                volumeControl.setValue(volumeReductionInDecibels);

                // Play the audio clip
                clip.start();

                // Allow the clip to play to completion
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
                System.err.println("Error playing sound file: " + soundURL);
            }
        } else {
            System.err.println("Error: soundURL is null");
        }
    }






    // Get Custom Button to Work hjhjh

    private int displayCusTime_break;
    private int displayCusTime_work;
    private void getCustomTime(int cusBreakTime, int cusWorkTime) {

        displayCusTime_work = cusWorkTime;
        displayCusTime_break = cusBreakTime;


        if (!breakTimePreset) {
            if (displayCusTime_work != 0) {
                customBtn.setText(Integer.toString(displayCusTime_work) + " mins");
            }else {
                customBtn.setText("Custom");
            }
        } else {
            if (displayCusTime_break != 0) {
                customBtn.setText(Integer.toString(displayCusTime_break) + " mins");
            }else {
                customBtn.setText("Custom");
            }
        }
    }

    @FXML
    private void onCustomClicked() throws IOException {
        if (!breakTimePreset){
            if (displayCusTime_work != 0) {
                storedWorkTimePreset = displayCusTime_work * 60; // Convert minutes to seconds
                newTime = storedWorkTimePreset;
            } else {
                gotosetting();
                return;
            }
        } else {
            if (displayCusTime_break != 0) {
                storedBreakTimePreset = displayCusTime_break * 60; // Convert minutes to seconds
                newTime = storedBreakTimePreset;
            } else {
                gotosetting();
                return;
            }
        }
        updateTimerTime(newTime);
    }



}


