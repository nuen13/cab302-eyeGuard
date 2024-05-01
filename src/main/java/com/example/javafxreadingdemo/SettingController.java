package com.example.javafxreadingdemo;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class SettingController {

    @FXML //  fx:id="themeColor"
    private ComboBox<String> themeColor; // Value injected by FXMLLoader

    @FXML //  fx:id="alarmSound"
    private ComboBox<String> alarmSound; // Value injected by FXMLLoader

    @FXML
    private AnchorPane rootPane; // Assuming you want to change the background of this AnchorPane

    @FXML
    public void initialize() {
        themeColor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateBackgroundColor(newVal); // Update background color when selection changes
        });
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
}
