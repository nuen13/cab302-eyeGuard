package com.example.javafxreadingdemo;

import javax.print.DocFlavor;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CustomSettingDAO {

    private Connection connection;

    public CustomSettingDAO() {
        connection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() {
        createCustomSettingTable();
    }

    // Create the customSetting table if it doesn't exist
    public void createCustomSettingTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS customSetting (" +
                            "user_id INTEGER, " +
                            "themeColor VARCHAR(50), " +
                            "soundAlert VARCHAR(50), " +
                            "breakTime INTEGER, " +
                            "workTime INTEGER, " +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
        } catch (SQLException ex) {
            System.err.println("Error while creating customSetting table: " + ex.getMessage());
        }
    }
    // Retrieve custom settings for a user
    public List<CustomSetting> getCustomSetting(int userId) {
        List<CustomSetting> settings = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT user_id, themeColor, soundAlert, breakTime, workTime FROM customSetting WHERE user_id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int retrievedUserId = rs.getInt("user_id");
                    String themeColor = rs.getString("themeColor");
                    String soundAlert = rs.getString("soundAlert");
                    int breakTime = rs.getInt("breakTime");
                    int workTime = rs.getInt("workTime");

                    CustomSetting setting = new CustomSetting(
                            retrievedUserId,
                            themeColor,
                            soundAlert,
                            breakTime,
                            workTime
                    );
                    settings.add(setting);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving custom settings: " + ex.getMessage());
        }
        return settings;
    }

//     Save custom settings for a user
    public void saveCustomSetting(int userId, String themeColor, String soundAlert, int breakTime, int workTime) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO customSetting (user_id, themeColor, soundAlert, breakTime, workTime) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setString(2, themeColor);
            statement.setString(3, soundAlert);
            statement.setInt(4, breakTime);
            statement.setInt(5, workTime);

            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error saving custom settings: " + ex.getMessage());
        }
    }

    // Update custom settings (if needed)
    // Delete custom settings (if needed)
}

