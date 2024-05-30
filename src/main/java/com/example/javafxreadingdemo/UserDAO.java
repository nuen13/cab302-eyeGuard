package com.example.javafxreadingdemo;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        connection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() {
        createUsersTable();
    }

    private void createUsersTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "email TEXT UNIQUE, " +
                            "password TEXT, " +
                            "day_streak INTEGER DEFAULT 0, " +
                            "last_login BIGINT DEFAULT 0)"  // Store as Unix timestamp
            );

            // Check if the last_login column exists, if not, add it
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)");
            boolean lastLoginExists = false;
            while (rs.next()) {
                if ("last_login".equals(rs.getString("name"))) {
                    lastLoginExists = true;
                    break;
                }
            }
            if (!lastLoginExists) {
                stmt.execute("ALTER TABLE users ADD COLUMN last_login BIGINT DEFAULT 0");
            }
        } catch (SQLException ex) {
            System.err.println("Error while creating or updating users table: " + ex.getMessage());
        }
    }

    public Integer validateUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(User user) {
        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDayStreak(int userId) {
        LocalDate today = LocalDate.now();
        long todayEpoch = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        try {
            connection.setAutoCommit(false);

            // Fetch the last login date
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT last_login, day_streak FROM users WHERE id = ?")) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    long lastLoginEpoch = rs.getLong("last_login");
                    int currentStreak = rs.getInt("day_streak");

                    LocalDate lastLoginDate = LocalDate.ofEpochDay(lastLoginEpoch / (24 * 60 * 60));

                    // Check if the last login was yesterday
                    if (lastLoginDate.equals(today)) {
                        // Already logged in today, skip updating
                        return;
                    } else if (lastLoginDate.plusDays(1).equals(today)) {
                        currentStreak++;
                    } else {
                        currentStreak = 1;
                    }

                    // Update the last login date and day streak
                    try (PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE users SET last_login = ?, day_streak = ? WHERE id = ?")) {
                        updateStmt.setLong(1, todayEpoch);
                        updateStmt.setInt(2, currentStreak);
                        updateStmt.setInt(3, userId);
                        updateStmt.executeUpdate();
                    }
                }
            }

            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
