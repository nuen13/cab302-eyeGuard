package com.example.javafxreadingdemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;


import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import javafx.scene.control.ComboBox;

import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.net.URL;
import java.time.LocalDate;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class SettingController {

    private CustomSettingDAO CustomSettingDAO;

    private Button breakTimeC;
    private Color newColor;
    @FXML
    private Button back;


    @FXML
    protected void backbutton() throws IOException {
        Stage stage = (Stage)this.back.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 600.0, 400.0);

        stage.setTitle("eyeGuard App");
        stage.setScene(scene);
        stage.show();

        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    @FXML //  fx:id="themeColor"
    private ComboBox<String> themeColor; // Value injected by FXMLLoader

    @FXML //  fx:id="alarmSound"
    private ComboBox<String> alarmSound;

    @FXML
    private AnchorPane rootPane;



    @FXML
    public void initialize() {


        themeColor.getSelectionModel().select(ShareVarSetting.colorActive);
        themeColor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateBackgroundColor(newVal);
        });
        setBackgroundTheme(ShareVarSetting.themeColor);

        alarmSound.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> getSound(newVal));

        System.out.println(ShareVarSetting.colorActive);
    }

    // Get Custom Timer
    // Retrieve Custom Timer and Send it to Db
    // WorkTime



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

    private TextField textFieldBreak;
//    @FXML
//    private void c_breakTimeClicked(ActionEvent event){
//        try {
//            int customTime = Integer.parseInt(textFieldBreak.getText());
//            if (customTime > 0) {
//                CustomSettingDAO.retrieveTimeSetting(customTime);
//            } else {
//                showAlert("Invalid Input", "Break interval must be greater than zero.");// Handle invalid input (negative or zero)
//            }
//        } catch (NumberFormatException e) {
//            showAlert("Invalid Input", "Please enter a valid number.");// Handle invalid input (not a number)
//            timerSet = false; //Ensure flag is false if invalid input
//        }
//    }

//    private void getBreakTime_c() {
//        if (secondsElapsed > 0 && userId > 0) {
//            userDAO.insertFocusSession(userId, LocalDate.now(), secondsElapsed);
//            secondsElapsed = 0;
//        }
//    }



    private void setBackgroundTheme (Color color ){
        BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        rootPane.setBackground(background);
    }
    private void updateBackgroundColor(String colorName) {
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
                    ShareVarSetting.themeColor = newColor = Color.LIGHTGOLDENRODYELLOW;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
                case "Winter":
                    ShareVarSetting.themeColor = newColor = Color.LIGHTBLUE;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
                case "Spring":
                    ShareVarSetting.themeColor = newColor = Color.LIGHTGREEN;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    break;
            }
        }
    }

    private Clip clip;

    private void getSound(String soundName){
        if ("no sound".equals(soundName)) {
            // Stop the previous sound if it's playing
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            return;
        }

        ShareVarSetting.alertSound = getClass().getResource("/soundEffect/" + soundName + ".wav");
        playSound(ShareVarSetting.alertSound);
    }
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
