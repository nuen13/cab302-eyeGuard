package com.example.javafxreadingdemo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Analytics extends Application{


        @Override
        public void start(Stage primaryStage) {
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
            Button monthButton = new Button("Months");
            monthButton.setId("monthButton");
            Button yearButton = new Button("Year");
            yearButton.setId("yearButton");
            timeSelection.getChildren().addAll(weekButton, monthButton, yearButton);

            // Content area
            Rectangle contentArea = new Rectangle(300, 200); // Placeholder for content
            contentArea.getStyleClass().add("content-area");
            contentArea.setArcWidth(20);
            contentArea.setArcHeight(20);


            root.getChildren().addAll(topBar, analyticsButtons, timeSelection, contentArea);

            // Show the stage
            Scene scene = new Scene(root, 500, 462);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            primaryStage.setTitle("Analytics");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        private Button createAnalyticsButton(String text, String value) {
            Button button = new Button(text + "\n" + value);
            button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
            button.getStyleClass().add("analytics-button");
            return button;
        }


}
