package com.example.javafxreadingdemo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.io.IOException;
import java.net.URL;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.time.LocalDate;
import java.util.List;

public class TimeAppController {
    // Timer variables
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int timeInterval = 60;
    private boolean timerSet = false;
    private boolean timerRun = false;
    private int timeInMinute = 0;
    private int newTime = 0;
    private boolean breakTimePreset = false;

    // UI elements
    @FXML
    private Label timerLabel;
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
    @FXML
    private Button setting;

    // Other variables
    private UserDAO userDAO;
    private int userId;
    private CustomSettingDAO customSettingDAO;
    private Clip clip;
    private int displayCusTime_break;
    private int displayCusTime_work;

    // Default constructor
    public TimeAppController() {
    }

    // Setter for user ID
    public void setUserId(int userId) {
        this.userId = userId;
    }
    @FXML
    private void initialize() {
        // Initialize DAOs
        customSettingDAO = new CustomSettingDAO();
        customSettingDAO.createCustomSettingTable();
        userDAO = new UserDAO();

        // Add logo to layout
        addLogoToLayout();

        // Initialize timer label
        newTime = 1;
        updateTimerTime(newTime);

        // Set initial styles for time buttons
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
    private void timerEnd(){
        if(breakTimePreset){
            alertText = "AAAAA .... Get Back To Workkkk";
            playSound(ShareVarSetting.alertSound);
            breakTimePreset = false;
            newTime = 2;

            saveFocusSession();
        }
        else {
            alertText = "Ring Ring... It is time for a Break";
            playSound(ShareVarSetting.alertSound);
            breakTimePreset = true;
            newTime = 4;

            saveFocusSession();
        }
        handleBreak();
        changeTimePreset(breakTimePreset);

        updateTimerTime(newTime);
    }

    // Display Alert Box
    private void handleBreak() {
        // Display an alert with the break message
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Break Time");
            alert.setHeaderText(null);
            alert.setContentText(alertText);
            alert.showAndWait();
        });
    }


    // Update the timer label with the elapsed time in the correct format
    private void updateTimerLabel() {
        int hours = secondsElapsed / 3600;
        int min = (secondsElapsed % 3600) / 60;
        int sec = secondsElapsed % 60;

        timerLabel.setText(String.format("%02d:%02d:%02d", hours, min, sec));
    }

    // Update the timer time to a new value and update the timer label and start button
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

    // Update the text of the start button based on whether the timer is running or paused
    private void updateStartBtn(boolean timerRun){
        if (timerRun){
            startBtn.setText("Pause");
        }
        else {
            startBtn.setText("Start");
        }
    }


    // Start or pause the timer based on current state
    @FXML
    private void StartTime(ActionEvent event) {
        if (!timerRun) {
            if (timerSet) {
                timerRun = true;

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
            } else {
                customBtn.setText("Custom");
            }
        } else {
            lowBtn.setText("Bursts (30 mins)");
            midBtn.setText("Grind (45 mins)");
            highBtn.setText("Cram (1 hour)");
            worktime.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-cursor: hand;");
            breaktime.setStyle("");

            if (displayCusTime_work != 0) {
                customBtn.setText(Integer.toString(displayCusTime_work) + " mins");
            } else {
                customBtn.setText("Custom");
            }
        }
    }


    @FXML
    private void onbreakTime(ActionEvent event){
        breakTimePreset = true;
        newTime = 600;

        updateTimerTime(newTime);
        changeTimePreset(breakTimePreset);
    }

    @FXML
    private void onworkTime(ActionEvent event){
        breakTimePreset = false;
        newTime = 1;

        updateTimerTime(newTime);
        changeTimePreset(breakTimePreset);
    }




    // go to analytic page
    @FXML
    private void onAnalyticsButtonClicked(ActionEvent event) {
        try {
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
        if (!breakTimePreset){newTime = 1800;}
        else {newTime = 600;}
        updateTimerTime(newTime);
    }

    @FXML
    private void onMidClicked(ActionEvent event){
        if (!breakTimePreset){newTime = 2700;}
        else {newTime = 1500;}
        updateTimerTime(newTime);
    }

    @FXML
    private void onHighClicked(ActionEvent event){
        if (!breakTimePreset){newTime = 3600;}
        else {newTime = 3600;}
        updateTimerTime(newTime);
    }
    @FXML
    private void onCustomClicked() throws IOException {
        if (!breakTimePreset){
            if (displayCusTime_work != 0) {newTime = displayCusTime_work * 60;}
            else {gotosetting();}
        } else {
            if (displayCusTime_break != 0) {newTime = displayCusTime_break * 60;}
            else {gotosetting();}
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
    protected void gotosetting() throws IOException {
        Stage stage = (Stage)this.setting.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("setting-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 600, 400);

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

    public void loadCustomSetting(int userId) {
        List<CustomSetting> customSettings = customSettingDAO.getCustomSetting(userId);
        for (CustomSetting setting : customSettings) {
            updateUIFromCustomSetting(setting);
            printSettingDebugInfo(setting);
        }
    }

    private void updateUIFromCustomSetting(CustomSetting setting) {
        updateBackgroundColor(setting.getThemeColor());
        getSound(setting.getSoundAlert());
        getCustomTime(setting.getBreakTime(), setting.getWorkTime());
    }

    private void printSettingDebugInfo(CustomSetting setting) {
        System.out.println("Theme Color: " + setting.getThemeColor());
        System.out.println("Sound Alert: " + setting.getSoundAlert());
        System.out.println("Break Time: " + setting.getBreakTime());
        System.out.println("Work Time: " + setting.getWorkTime());
    }

    private void setBackgroundTheme(Color color) {
        BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        rootPane.setBackground(background);
    }

    public void updateBackgroundColor(String colorName) {
        System.out.println("aaaa " + colorName);
        if (colorName != null) {
            switch (colorName) {
                case "Default":
                    setThemeColor(Color.rgb(0, 9, 19));
                    break;
                case "Summer":
                    setThemeColor(Color.LIGHTCORAL);
                    break;
                case "Autumn":
                    setThemeColor(Color.LIGHTGOLDENRODYELLOW);
                    break;
                case "Winter":
                    setThemeColor(Color.LIGHTBLUE);
                    break;
                case "Spring":
                    setThemeColor(Color.LIGHTGREEN);
                    break;
            }
        }
    }

    private void setThemeColor(Color color) {
        ShareVarSetting.themeColor = color;
        setBackgroundTheme(color);
        ShareVarSetting.colorActive = color.toString(); // Assuming color.toString() gives the color name
    }


    private void getSound(String soundName) {
        if ("Default".equals(soundName)) {
            stopAndCloseClip();
            return;
        }

        ShareVarSetting.soundName = soundName;
        ShareVarSetting.alertSound = getClass().getResource("/soundEffect/" + soundName + ".wav");
    }

    private void playSound(URL soundURL) {
        if (soundURL != null) {
            stopAndCloseClip();

            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                handleSoundException(e, soundURL);
            }
        }
    }

    private void stopAndCloseClip() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    private void handleSoundException(Exception e, URL soundURL) {
        e.printStackTrace();
        System.err.println("Error playing sound file: " + soundURL);
    }

    private void getCustomTime(int cusBreakTime, int cusWorkTime) {
        displayCusTime_work = cusWorkTime;
        displayCusTime_break = cusBreakTime;

        int displayTime = breakTimePreset ? displayCusTime_break : displayCusTime_work;
        String buttonText = (displayTime != 0) ? (displayTime + " mins") : "Custom";
        customBtn.setText(buttonText);
    }


}


