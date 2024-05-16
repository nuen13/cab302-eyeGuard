package com.example.javafxreadingdemo;

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

public class Analytics extends Application {
    private int loggedInUserId;
    private UserDAO userDAO;
    private BarChart<String, Number> barChart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;

    public void setUserId(int userId) {
        this.loggedInUserId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        userDAO = new UserDAO();
        userDAO.createFocusSessionTable();

        // Main layout is a VBox
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(15));
        root.getStyleClass().add("root");

        // Top part of the interface using a BorderPane
        BorderPane topBar = new BorderPane();
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/Logo/Logo.jpg.png")));
        logoView.setFitHeight(100);  // Increase the height for a bigger image
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
        String dayStreak = userDAO.getDayStreak(loggedInUserId);
        String timeFocused = formatTime(userDAO.getTotalFocusDuration(loggedInUserId));
        String daysAccessed = userDAO.getDaysAccessed(loggedInUserId);

        Button hoursFocused = createAnalyticsButton("Time Focused:", timeFocused);
        Button daysAccessedButton = createAnalyticsButton("Days Accessed:", daysAccessed);
        Button dayStreakButton = createAnalyticsButton("Day Streak:", dayStreak);
        analyticsButtons.getChildren().addAll(hoursFocused, daysAccessedButton, dayStreakButton);

        // Bottom part of the interface - Time selection
        HBox timeSelection = new HBox(10);
        timeSelection.setAlignment(Pos.CENTER);
        Button minutesButton = new Button("Minutes");
        minutesButton.setId("minutesButton");
        Button hoursButton = new Button("Hours");
        hoursButton.setId("hoursButton");
        Button daysButton = new Button("Days");
        daysButton.setId("daysButton");
        timeSelection.getChildren().addAll(minutesButton, hoursButton, daysButton);

        // Content area - Graph
        barChart = createBarChart();

        root.getChildren().addAll(topBar, analyticsButtons, timeSelection, barChart);

        // Show the stage
        Scene scene = new Scene(root, 725, 460);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Analytics");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load the focus sessions data for the default view (e.g., Hours)
        loadFocusSessionsData("Hours");

        // Set button actions
        minutesButton.setOnAction(event -> loadFocusSessionsData("Minutes"));
        hoursButton.setOnAction(event -> loadFocusSessionsData("Hours"));
        daysButton.setOnAction(event -> loadFocusSessionsData("Days"));
    }

    private Button createAnalyticsButton(String text, String value) {
        Button button = new Button(text + "\n" + value);
        button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        button.getStyleClass().add("analytics-button");
        return button;
    }

    private BarChart<String, Number> createBarChart() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis(0, 1, 0.15); //Graph's y-axis measuring 1 hour in 15min increments
        xAxis.setLabel("Session Date");//
        yAxis.setLabel("Focus Time (hours)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Focus Hours");
        return barChart;
    }

    private void loadFocusSessionsData(String period) {
        // Fetch the focus sessions data from the database
        List<FocusSession> focusSessions = userDAO.getFocusSessionsByUserId(loggedInUserId);

        // Clear any existing data in the chart
        barChart.getData().clear();

        if (focusSessions.isEmpty()) {
            return;
        }

        // Aggregate focus durations by session date
        Map<LocalDate, Integer> aggregatedData = focusSessions.stream()
                .collect(Collectors.groupingBy(
                        FocusSession::getSessionDate,
                        Collectors.summingInt(FocusSession::getFocusDuration)
                ));

        // Create a new series to hold the data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Time Focused");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

        aggregatedData.forEach((date, duration) -> {
            String dateString = date.format(formatter);
            series.getData().add(new XYChart.Data<>(dateString, duration / 3600.0)); // Convert seconds to hours
        });

        // Add the series to the chart
        barChart.getData().add(series);
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
