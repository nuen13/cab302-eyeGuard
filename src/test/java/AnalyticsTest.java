/*package com.example.javafxreadingdemo;

import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AnalyticsTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Start the application
        Analytics analytics = new Analytics();
        analytics.setUserId(1); // Set a user ID for testing
        analytics.start(stage);
    }

    @Test
    public void testLoadFocusSessionsData() {
        // Test loading focus session data for different periods
        clickOn("#hoursButton");
        BarChart<String, Number> barChart = lookup("#chart").query();
        assertNotNull(barChart);
        assertEquals("Focus Hours", barChart.getTitle());
        // Add more assertions based on expected data for hours period

        clickOn("#daysButton");
        // Test loading focus session data for days period
        // Add assertions based on expected data for days period
    }

    @Test
    public void testButtonActions() {
        // Test button actions
        Button minutesButton = lookup("#minutesButton").query();
        Button hoursButton = lookup("#hoursButton").query();
        Button daysButton = lookup("#daysButton").query();

        assertNotNull(minutesButton);
        assertNotNull(hoursButton);
        assertNotNull(daysButton);

        // Simulate button clicks
        clickOn(minutesButton);
        // Test expected behavior after clicking minutesButton
        // Add assertions based on expected behavior

        clickOn(hoursButton);
        // Test expected behavior after clicking hoursButton
        // Add assertions based on expected behavior

        clickOn(daysButton);
        // Test expected behavior after clicking daysButton
        // Add assertions based on expected behavior
    }
}
*/