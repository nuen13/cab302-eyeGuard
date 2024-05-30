package com.example.javafxreadingdemo;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Analytics extends Application {
    private int loggedInUserId;
    private AnalyticsDAO analyticsDAO;
    private UserDAO userDAO;
    private CustomSettingDAO customSettingDAO;
    private BarChart<String, Number> barChart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private VBox root;

    public void setUserId(int userId) {
        this.loggedInUserId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        analyticsDAO = new AnalyticsDAO();
        userDAO = new UserDAO(); // Initialize UserDAO
        customSettingDAO = new CustomSettingDAO(); // Initialize CustomSettingDAO

        // Main layout is a VBox
        root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(15));
        // Apply user settings (background color)
        applyUserSettings();

        // Top part of the interface using a BorderPane
        BorderPane topBar = new BorderPane();
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/Logo/Logo.jpg.png")));
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);
        topBar.setCenter(logoView);
        BorderPane.setAlignment(logoView, Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setId("backButton");
        topBar.setLeft(backButton);
        BorderPane.setMargin(backButton, new Insets(10));

        backButton.setOnAction(event -> {
            try {
                TimerManagementApplication app = new TimerManagementApplication();
                app.setUserId(loggedInUserId);
                app.start(new Stage());
                Stage currentStage = (Stage) backButton.getScene().getWindow();
                currentStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        HBox analyticsButtons = new HBox(10);
        analyticsButtons.setAlignment(Pos.CENTER);

        // Fetch data from the database
        String dayStreak = analyticsDAO.getDayStreak(loggedInUserId);
        String workTime = formatTime(analyticsDAO.getTotalWorkTime(loggedInUserId));
        String breakTime = formatTime(analyticsDAO.getTotalBreakTime(loggedInUserId));
        String daysAccessed = analyticsDAO.getDaysAccessed(loggedInUserId);

        Button workTimeButton = createAnalyticsButton("Work Time:", workTime);
        Button breakTimeButton = createAnalyticsButton("Break Time:", breakTime);
        Button daysAccessedButton = createAnalyticsButton("Days Accessed:", daysAccessed);
        Button dayStreakButton = createAnalyticsButton("Day Streak:", dayStreak);
        analyticsButtons.getChildren().addAll(workTimeButton, breakTimeButton, daysAccessedButton, dayStreakButton);

        // Bottom part of the interface - Time selection
        HBox timeSelection = new HBox(10);
        timeSelection.setAlignment(Pos.CENTER);
        Button workButton = new Button("Work Time");
        workButton.setId("workButton");
        Button bothButton = new Button("Both");
        bothButton.setId("bothButton");
        Button breakButton = new Button("Break Time");
        breakButton.setId("breakButton");
        timeSelection.getChildren().addAll(workButton, bothButton, breakButton);

        // Content area - Graph
        barChart = createBarChart();

        root.getChildren().addAll(topBar, analyticsButtons, timeSelection, barChart);

        // Show the stage
        Scene scene = new Scene(root, 900, 450);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Analytics");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Update analytics data and load the focus sessions data for the default view
        updateAnalyticsData();
        loadFocusSessionsData("Work Time");

        // Set button actions
        workButton.setOnAction(event -> loadFocusSessionsData("Work Time"));
        bothButton.setOnAction(event -> loadFocusSessionsData("Both"));
        breakButton.setOnAction(event -> loadFocusSessionsData("Break Time"));
    }

    private void applyUserSettings() {
        List<CustomSetting> customSettings = customSettingDAO.getCustomSetting(loggedInUserId);
        for (CustomSetting setting : customSettings) {
            updateBackgroundColor(setting.getThemeColor());
        }
    }

    private void updateBackgroundColor(String colorName) {
        if (colorName != null) {
            Color color;
            switch (colorName) {
                case "Summer":
                    color = Color.LIGHTCORAL;
                    break;
                case "Autumn":
                    color = Color.LIGHTGOLDENRODYELLOW;
                    break;
                case "Winter":
                    color = Color.LIGHTBLUE;
                    break;
                case "Spring":
                    color = Color.LIGHTGREEN;
                    break;
                default:
                    color = Color.rgb(0, 9, 19);
            }
            setBackgroundTheme(color);
        }
    }

    private void setBackgroundTheme(Color color) {
        BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        root.setBackground(background);
    }

    private void updateAnalyticsData() {
        userDAO.updateDayStreak(loggedInUserId);
    }

    private Button createAnalyticsButton(String text, String value) {
        Button button = new Button(text + "\n" + value);
        button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        button.getStyleClass().add("analytics-button");
        return button;
    }

    private BarChart<String, Number> createBarChart() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Session Date");
        yAxis.setLabel("Focus Time (hours)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Focus Hours");
        barChart.setLegendVisible(true); // Make sure legend is visible
        return barChart;
    }

    private void loadFocusSessionsData(String type) {
        // Fetch the focus sessions data from the database
        List<FocusSession> focusSessions = analyticsDAO.getFocusSessionsByUserId(loggedInUserId);

        // Clear any existing data in the chart
        barChart.getData().clear();

        if (focusSessions.isEmpty()) {
            return;
        }

        // Aggregate focus durations by session date
        Map<LocalDate, Integer> aggregatedWorkData = focusSessions.stream()
                .collect(Collectors.groupingBy(
                        FocusSession::getSessionDate,
                        Collectors.summingInt(FocusSession::getWorkTime)
                ));

        Map<LocalDate, Integer> aggregatedBreakData = focusSessions.stream()
                .collect(Collectors.groupingBy(
                        FocusSession::getSessionDate,
                        Collectors.summingInt(FocusSession::getBreakTime)
                ));

        // Create new series to hold the data
        XYChart.Series<String, Number> workSeries = new XYChart.Series<>();
        workSeries.setName("Work Time");

        XYChart.Series<String, Number> breakSeries = new XYChart.Series<>();
        breakSeries.setName("Break Time");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

        if (type.equals("Work Time") || type.equals("Both")) {
            aggregatedWorkData.forEach((date, duration) -> {
                String dateString = date.format(formatter);
                double timeInHours = duration / 3600.0; // Convert seconds to hours
                yAxis.setLabel("Time (hours)");
                workSeries.getData().add(new XYChart.Data<>(dateString, timeInHours));
            });
        }

        if (type.equals("Break Time") || type.equals("Both")) {
            aggregatedBreakData.forEach((date, duration) -> {
                String dateString = date.format(formatter);
                double timeInHours = duration / 3600.0; // Convert seconds to hours
                yAxis.setLabel("Time (hours)");
                breakSeries.getData().add(new XYChart.Data<>(dateString, timeInHours));
            });
        }

        // Add the series to the chart based on the selected type
        if (type.equals("Work Time")) {
            barChart.getData().add(workSeries);
        } else if (type.equals("Break Time")) {
            barChart.getData().add(breakSeries);
        } else if (type.equals("Both")) {
            barChart.getData().addAll(workSeries, breakSeries);
        }
    }

    private String formatTime(int totalSeconds) {
        long days = TimeUnit.SECONDS.toDays(totalSeconds);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60;
        long seconds = totalSeconds % 60;

        return String.format("%d Days %d Hours %d Minutes %d Seconds", days, hours, minutes, seconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
