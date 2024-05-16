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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class SettingController {
    @FXML
    private TextField breakCustom;
    @FXML
    private TextField workCustom;
    @FXML
    private Button back;
    @FXML
    private ComboBox<String> themeColor;
    @FXML
    private ComboBox<String> alarmSound;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label yayText1;
    @FXML
    private Label yayText2;
    private int userId;
    private Clip clip;
    private Connection connection;
    private CustomSettingDAO customSettingDAO;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private void initializeDatabase() {
        customSettingDAO = new CustomSettingDAO();
        customSettingDAO.createCustomSettingTable();
    }

    @FXML
    public void initialize() {
        // Initialize database
        initializeDatabase();

        // Set up theme color dropdown
        themeColor.getSelectionModel().select(ShareVarSetting.colorActive);
        themeColor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateBackgroundColor(newVal);
        });

        // Set initial background theme
        setBackgroundTheme(ShareVarSetting.themeColor);

        // Set up alarm sound dropdown
        alarmSound.getSelectionModel().select(ShareVarSetting.soundName);
        alarmSound.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> manageSound(newVal));

        // Debug: print active color
        System.out.println(ShareVarSetting.colorActive);
    }


    private void updateSettingTime(int time, String setting) {
        try (PreparedStatement updateUser = connection.prepareStatement(
                "UPDATE customSetting SET " + setting + " = ? WHERE user_id = ?")) {
            updateUser.setInt(1, time);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    private boolean isValidInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateCustomTime(String input, Label label) {
        if (isValidInput(input)) {
            int customTime = Integer.parseInt(input);
            String setting = label.getId().equals("yayText1") ? "breakTime" : "workTime";
            updateSettingTime(customTime, setting);
            label.setText("New time is added - " + customTime + " mins");
            label.setStyle("-fx-text-fill: green;");
        } else {
            label.setText("Time must be greater than 0!");
            label.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void breakCustomClicked(ActionEvent actionEvent) {
        updateCustomTime(breakCustom.getText(), yayText1);
    }

    @FXML
    public void workCustomClicked(ActionEvent actionEvent) {
        updateCustomTime(workCustom.getText(), yayText2);
    }



    private void updateSetting(String settingName, String value) {
        String updateQuery = "UPDATE customSetting SET " + settingName + " = ? WHERE user_id = ?";
        try (PreparedStatement updateUser = connection.prepareStatement(updateQuery)) {
            updateUser.setString(1, value);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    private void updateSoundSetting(String soundName) {
        updateSetting("soundAlert", soundName);
    }

    private void updateCustomSetting(String themeColor) {
        updateSetting("themeColor", themeColor);
    }


    private void setBackgroundTheme(Color color) {
        BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        rootPane.setBackground(background);
    }

    public void updateBackgroundColor(String colorName) {
        if (colorName != null) {
            Color newColor;
            switch (colorName) {
                case "Default":
                    newColor = Color.rgb(0, 9, 19);
                    break;
                case "Summer":
                    newColor = Color.LIGHTCORAL;
                    break;
                case "Autumn":
                    newColor = Color.LIGHTGOLDENRODYELLOW;
                    break;
                case "Winter":
                    newColor = Color.LIGHTBLUE;
                    break;
                case "Spring":
                    newColor = Color.LIGHTGREEN;
                    break;
                default:
                    // Handle unexpected color names
                    return;
            }
            ShareVarSetting.themeColor = newColor;
            setBackgroundTheme(newColor);
            ShareVarSetting.colorActive = colorName;
            updateCustomSetting(colorName); // Update the database with the new theme color
        }
    }



    private void manageSound(String soundName) {
        if ("Default".equals(soundName)) {
            stopCurrentSound();
        } else {
            playNewSound(soundName);
        }
    }

    private void stopCurrentSound() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    private void playNewSound(String soundName) {
        try {
            ShareVarSetting.soundName = soundName;
            updateSoundSetting(soundName);
            URL soundURL = getClass().getResource("/soundEffect/" + soundName + ".wav");
            if (soundURL == null) {
                return;
            }
            stopCurrentSound();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
        } catch (IOException e) {
        } catch (LineUnavailableException e) {
        }
    }

    @FXML
    protected void onBackButtonClicked() throws IOException {
        Stage stage = (Stage)this.back.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 600, 400);

        TimeAppController controller = fxmlLoader.getController();
        controller.loadCustomSetting(userId);
        controller.setUserId(userId);


        stage.setTitle("eyeGuard App");
        stage.setScene(scene);
        stage.show();

        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }



}
