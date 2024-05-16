package com.example.javafxreadingdemo;

import javax.print.DocFlavor;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
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

    public void createCustomSettingTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS customSetting (" +
                "user_id INTEGER, " +
                "themeColor VARCHAR(50), " +
                "soundAlert VARCHAR(50), " +
                "breakTime INTEGER, " +
                "workTime INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException ex) {
            System.err.println("Error creating customSetting table: " + ex.getMessage());
        }
    }

    public List<CustomSetting> getCustomSetting(int userId) {
        List<CustomSetting> settings = new ArrayList<>();
        String selectQuery = "SELECT user_id, themeColor, soundAlert, breakTime, workTime FROM customSetting WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int retrievedUserId = rs.getInt("user_id");
                    String themeColor = rs.getString("themeColor");
                    String soundAlert = rs.getString("soundAlert");
                    int breakTime = rs.getInt("breakTime");
                    int workTime = rs.getInt("workTime");

                    CustomSetting setting = new CustomSetting(retrievedUserId, themeColor, soundAlert, breakTime, workTime);
                    settings.add(setting);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving custom settings: " + ex.getMessage());
        }
        return settings;
    }

    public void saveCustomSetting(int userId, String themeColor, String soundAlert, int breakTime, int workTime) {
        String insertQuery = "INSERT INTO customSetting (user_id, themeColor, soundAlert, breakTime, workTime) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
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
}
