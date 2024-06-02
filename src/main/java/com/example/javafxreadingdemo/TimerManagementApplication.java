package com.example.javafxreadingdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class TimerManagementApplication extends Application {
    private int userId;

    public TimerManagementApplication() {}

    public TimerManagementApplication(int userId) {
        this.userId = userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimerManagementApplication.class.getResource("timer-view.fxml"));
        Parent root = fxmlLoader.load();

        TimeAppController controller = fxmlLoader.getController();
        controller.loadCustomSetting(userId);
        controller.setUserId(userId);

        Scene scene = new Scene(root, 600.0, 400.0);
        stage.setTitle("eyeGuard App");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}


