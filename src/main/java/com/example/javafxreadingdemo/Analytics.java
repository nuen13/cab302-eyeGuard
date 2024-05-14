package com.example.javafxreadingdemo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Analytics extends Application {

    private UserDAO userDAO;
    private LineChart<Number, Number> lineChart;
    private int loggedInUserId;
    private NumberAxis xAxis;
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
        Label titleLabel = new Label("Analytics");
        titleLabel.setId("titleLabel");
        topBar.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

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
        Button hoursFocused = createAnalyticsButton("Hours Focused", "....");
        Button daysAccessed = createAnalyticsButton("Days Accessed", "....");
        Button dayStreak = createAnalyticsButton("Day Streak", "....");
        analyticsButtons.getChildren().addAll(hoursFocused, daysAccessed, dayStreak);

        // Bottom part of the interface - Time selection
        HBox timeSelection = new HBox(10);
        timeSelection.setAlignment(Pos.CENTER);
        Button weekButton = new Button("Week");
        weekButton.setId("weekButton");
        Button monthButton = new Button("Month");
        monthButton.setId("monthButton");
        Button yearButton = new Button("Year");
        yearButton.setId("yearButton");
        timeSelection.getChildren().addAll(weekButton, monthButton, yearButton);

        // Content area - Graph
        lineChart = createLineChart();

        root.getChildren().addAll(topBar, analyticsButtons, timeSelection, lineChart);

        // Show the stage
        Scene scene = new Scene(root, 500, 462);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Analytics");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load the focus sessions data for the default view (e.g., month)
        loadFocusSessionsData("Month");

        // Set button actions
        weekButton.setOnAction(event -> loadFocusSessionsData("Week"));
        monthButton.setOnAction(event -> loadFocusSessionsData("Month"));
        yearButton.setOnAction(event -> loadFocusSessionsData("Year"));
    }

    private Button createAnalyticsButton(String text, String value) {
        Button button = new Button(text + "\n" + value);
        button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        button.getStyleClass().add("analytics-button");
        return button;
    }

    private LineChart<Number, Number> createLineChart() {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Hours Focused");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Focus Hours");
        return lineChart;
    }

    private void loadFocusSessionsData(String period) {
        // Fetch the focus sessions data from the database
        List<FocusSession> focusSessions = userDAO.getFocusSessionsByUserId(loggedInUserId);

        // Clear any existing data in the chart
        lineChart.getData().clear();

        if (focusSessions.isEmpty()) {
            return;
        }

        // Create a new series to hold the data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Focus Hours");

        // Determine the start date based on the selected period
        LocalDate today = LocalDate.now();
        LocalDate startDate = today;
        if (period.equals("Week")) {
            startDate = today.minusDays(6);
        } else if (period.equals("Month")) {
            startDate = today.minusDays(29);
        } else if (period.equals("Year")) {
            startDate = today.minusDays(364);
        }

        // Find min and max values for the axes
        double maxDuration = 0;
        long minDay = Long.MAX_VALUE;
        long maxDay = Long.MIN_VALUE;

        for (FocusSession session : focusSessions) {
            LocalDate sessionDate = session.getSessionDate();
            if (!sessionDate.isBefore(startDate) && !sessionDate.isAfter(today)) {
                int focusDuration = session.getFocusDuration();
                long day = sessionDate.toEpochDay() - startDate.toEpochDay();
                series.getData().add(new XYChart.Data<>(day, focusDuration / 60.0)); // Convert minutes to hours

                if (focusDuration / 60.0 > maxDuration) {
                    maxDuration = focusDuration / 60.0;
                }
                if (day < minDay) {
                    minDay = day;
                }
                if (day > maxDay) {
                    maxDay = day;
                }
            }
        }

        // Adjust the axes based on the data
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(minDay);
        xAxis.setUpperBound(maxDay);
        xAxis.setTickUnit((maxDay - minDay) / 10.0);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxDuration);
        yAxis.setTickUnit(maxDuration / 10.0);

        // Add the series to the chart
        lineChart.getData().add(series);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
