package com.example.javafxreadingdemo;

import java.time.LocalDate;

public class TestDataSetup {

    private UserDAO userDAO;

    public TestDataSetup() {
        userDAO = new UserDAO();
    }

    public void setupTestData() {
        // Ensure the test user email is unique by deleting the user if it already exists
        User existingUser = userDAO.getUserByEmail("testuser@example.com");
        if (existingUser != null) {
            userDAO.delete(existingUser.getId());
        }

        // Insert a test user
        User testUser = new User("testuser@example.com", "password123");
        userDAO.insert(testUser);

        // Insert focus sessions for the test user over different periods
        int userId = testUser.getId();
        LocalDate today = LocalDate.now();

        // Insert focus sessions for the past week
        for (int i = 0; i < 7; i++) {
            userDAO.insertFocusSession(userId, today.minusDays(i), 60 + (i * 10)); // 60, 70, 80, etc. minutes
        }

        // Insert focus sessions for the past month
        for (int i = 0; i < 30; i++) {
            userDAO.insertFocusSession(userId, today.minusDays(i + 7), 90 + (i * 5)); // 90, 95, 100, etc. minutes
        }

        // Insert focus sessions for the past year
        for (int i = 0; i < 365; i += 10) {
            userDAO.insertFocusSession(userId, today.minusDays(i + 37), 120 + (i * 2)); // 120, 122, 124, etc. minutes
        }
    }

    public static void main(String[] args) {
        TestDataSetup testDataSetup = new TestDataSetup();
        testDataSetup.setupTestData();
    }
}
