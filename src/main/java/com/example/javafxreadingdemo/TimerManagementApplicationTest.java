package com.example.javafxreadingdemo;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class TimerManagementApplicationTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        TimerManagementApplication app = new TimerManagementApplication();
        app.start(stage);
    }

    @BeforeEach
    public void setUp() {
        clickOn("#breakIntervalField").write("60");
        clickOn("#setBreakIntervalButton");
    }

    @Test
    public void testTimerInitialization() {
        verifyThat("#timerLabel", LabeledMatchers.hasText("Time: 00:00:00 | Break Interval: 60 sec"));
    }

    @Test
    public void testStartButtonClickedWithoutIntervalSet() {
        clickOn("#onResetButton");
        clickOn("#startButton");
        verifyThat("Please set the break interval before starting the timer.", LabeledMatchers.hasText());
    }

    @Test
    public void testStartButtonClickedWithIntervalSet() {
        clickOn("#startButton");
        // Add assertions to test timer start
    }

    @Test
    public void testPauseButtonClicked() {
        clickOn("#startButton");
        clickOn("#onPauseButton");
        // Add assertions to test timer pause
    }

    @Test
    public void testResetButtonClicked() {
        clickOn("#onResetButton");
        // Add assertions to test timer reset
    }

    @Test
    public void testAnalyticsButtonClicked() {
        clickOn("#onAnalyticsButton");
        // Add assertions to test analytics window opening
    }

    @Test
    public void testSetBreakIntervalButtonClicked() {
        clickOn("#breakIntervalField").write("120");
        clickOn("#setBreakIntervalButton");
        verifyThat("#timerLabel", LabeledMatchers.hasText("Time: 00:00:00 | Break Interval: 120 sec"));
    }

}
