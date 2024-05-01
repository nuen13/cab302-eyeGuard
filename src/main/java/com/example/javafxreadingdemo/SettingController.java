package com.example.javafxreadingdemo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import javafx.scene.control.ComboBox;

import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.net.URL;

public class SettingController {

    @FXML
    private Button back;
    @FXML
    protected void backbutton() throws IOException {
        Stage stage = (Stage)this.back.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 640.0, 360.0);
        stage.setScene(scene);
    }

    @FXML //  fx:id="themeColor"
    private ComboBox<String> themeColor; // Value injected by FXMLLoader

    @FXML //  fx:id="alarmSound"
    private ComboBox<String> alarmSound;

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {
        themeColor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateBackgroundColor(newVal);
        });

        alarmSound.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> playSound(newVal));
    }

    private void updateBackgroundColor(String colorName) {
        if (colorName != null) {
            switch (colorName) {
                case "Summer":
                    rootPane.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
                case "Autumn":
                    rootPane.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
                case "Winter":
                    rootPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
                case "Spring":
                    rootPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    break;
            }
        }
    }

    private Clip clip;
    private void playSound(String soundName) {
        if ("no sound".equals(soundName)) {
            // Stop the previous sound if it's playing
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            return;
        }

        if (soundName != null) {
            try {
                // Stop the previous sound if it's playing
                if (clip != null) {
                    clip.stop();
                    clip.close();
                }

                // Load the audio file
                URL soundURL = getClass().getResource("/soundEffect/" + soundName + ".wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);

                // Get a sound clip resource
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // Play the audio clip
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
                System.err.println("Error playing sound file: " + soundName);
            }
        }
    }

}
