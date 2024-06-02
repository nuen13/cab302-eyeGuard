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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class SettingController {

    private CustomSettingDAO customSettingDAO;

    private Button breakTimeC;
    private Color newColor;
    @FXML
    private Button back;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    protected void backbutton() throws IOException {
        Stage stage = (Stage)this.back.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 600.0, 400.0);

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

    @FXML //  fx:id="themeColor"
    private ComboBox<String> themeColor; // Value injected by FXMLLoader

    @FXML //  fx:id="alarmSound"
    private ComboBox<String> alarmSound;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label yayText2;
    @FXML
    private Label yayText1;
    private Connection connection;

    private void initializeDatabase() {
        customSettingDAO = new CustomSettingDAO();
        customSettingDAO.createCustomSettingTable();
    }

    @FXML
    public void initialize() {
        connection = DatabaseConnection.getInstance();
        initializeDatabase();

        themeColor.getSelectionModel().select(ShareVarSetting.colorActive);
        themeColor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateBackgroundColor(newVal);
        });
        setBackgroundTheme(ShareVarSetting.themeColor);

        alarmSound.getSelectionModel().select(ShareVarSetting.soundName);
        alarmSound.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> getSound(newVal));
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private TextField breakCustom;
    @FXML
    private TextField workCustom;

    protected void updateBreakTime(int time) {
        try (PreparedStatement updateUser = connection.prepareStatement(
                "UPDATE customSetting SET breakTime = ? WHERE user_id = ?")) {
            updateUser.setInt(1, time);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    @FXML
    public void breakCustomClicked(ActionEvent actionEvent) {
        try {
            int customTime = Integer.parseInt(breakCustom.getText());
            if (customTime > 0) {
                updateBreakTime(customTime);
                yayText1.setText("New time is added - " + customTime + " mins");
                yayText1.setStyle("-fx-text-fill: green;");
            } else {
                yayText1.setText(" time must be greater than 0! ");
                yayText1.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            yayText1.setText("invalid input! ");
            yayText1.setStyle("-fx-text-fill: red;");
        }
    }

    protected void updateWorkTime(int time) {
        try (PreparedStatement updateUser = connection.prepareStatement(
                "UPDATE customSetting SET workTime = ? WHERE user_id = ?")) {
            updateUser.setInt(1, time);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    @FXML
    public void workCustomClicked(ActionEvent actionEvent) {
        try {
            int customTime = Integer.parseInt(workCustom.getText());
            if (customTime > 0) {
                updateWorkTime(customTime);
                yayText2.setText("New time is added - " + customTime + " mins");
                yayText2.setStyle("-fx-text-fill: green;");
            } else {
                yayText2.setText(" time must be greater than 0! ");
                yayText2.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            yayText2.setText("invalid input! ");
            yayText2.setStyle("-fx-text-fill: red;");
        }
    }

    protected void setBackgroundTheme(Color color) {
        BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        rootPane.setBackground(background);
    }

    protected void updateSoundSetting(String soundName) {
        try (PreparedStatement updateUser = connection.prepareStatement(
                "UPDATE customSetting SET soundAlert = ? WHERE user_id = ?")) {
            updateUser.setString(1, soundName);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    protected void updateCustomSetting(String soundName) {
        try (PreparedStatement updateUser = connection.prepareStatement(
                "UPDATE customSetting SET themeColor = ? WHERE user_id = ?")) {
            updateUser.setString(1, soundName);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    public void updateBackgroundColor(String colorName) {
        if (colorName != null) {
            switch (colorName) {
                case "Default":
                    ShareVarSetting.themeColor = Color.rgb(0, 9, 19);
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    updateCustomSetting(colorName);
                    break;
                case "Summer":
                    ShareVarSetting.themeColor = Color.LIGHTCORAL;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    updateCustomSetting(colorName);
                    break;
                case "Autumn":
                    ShareVarSetting.themeColor = Color.rgb(217, 156, 19);
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    updateCustomSetting(colorName);
                    break;
                case "Winter":
                    ShareVarSetting.themeColor = Color.LIGHTBLUE;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    updateCustomSetting(colorName);
                    break;
                case "Spring":
                    ShareVarSetting.themeColor = Color.LIGHTGREEN;
                    setBackgroundTheme(ShareVarSetting.themeColor);
                    ShareVarSetting.colorActive = colorName;
                    updateCustomSetting(colorName);
                    break;
            }
        }
    }


    private Clip clip;

    protected void getSound(String soundName){
        if ("Default".equals(soundName)) {
            // Stop the previous sound if it's playing
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            return;
        }

        ShareVarSetting.soundName = soundName;
        updateSoundSetting(soundName);
        ShareVarSetting.alertSound = getClass().getResource("/soundEffect/" + soundName + ".wav");
        playSound(ShareVarSetting.alertSound);

    }
    protected void playSound(URL soundURL) {
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