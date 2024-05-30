package com.example.javafxreadingdemo;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsDAO {
    private Connection connection;

    public AnalyticsDAO() {
        connection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() {
        createFocusSessionTable();
    }

    public void createFocusSessionTable() {
        try (Statement stmt = connection.createStatement()) {
            // Create the focus_sessions table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS focus_sessions (" +
                            "session_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "user_id INTEGER, " +
                            "session_date BIGINT, " + // Store as Unix timestamp
                            "work_time INTEGER, " +  // New field for work time
                            "break_time INTEGER, " +  // New field for break time
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
        } catch (SQLException ex) {
            System.err.println("Error while creating focus_sessions table: " + ex.getMessage());
        }
    }

    public void insertFocusSession(int userId, LocalDate sessionDate, int workTime, int breakTime) {
        long sessionDateAsLong = sessionDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO focus_sessions (user_id, session_date, work_time, break_time) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setLong(2, sessionDateAsLong);
            statement.setInt(3, workTime);
            statement.setInt(4, breakTime);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error inserting focus session: " + ex.getMessage());
        }
    }

    public void updateFocusSession(int userId, LocalDate sessionDate, int duration, boolean isBreakTime) {
        long sessionDateAsLong = sessionDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        String column = isBreakTime ? "break_time" : "work_time";
        String query = "UPDATE focus_sessions SET " + column + " = " + column + " + ? WHERE user_id = ? AND session_date = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, duration);
            statement.setInt(2, userId);
            statement.setLong(3, sessionDateAsLong);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                insertFocusSession(userId, sessionDate, isBreakTime ? 0 : duration, isBreakTime ? duration : 0);
            }
        } catch (SQLException ex) {
            System.err.println("Error updating focus session: " + ex.getMessage());
        }
    }

    public List<FocusSession> getFocusSessionsByUserId(int userId) {
        List<FocusSession> sessions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT session_id, user_id, session_date, work_time, break_time FROM focus_sessions WHERE user_id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long sessionDateAsLong = rs.getLong("session_date");
                    LocalDate sessionDate = Instant.ofEpochSecond(sessionDateAsLong).atZone(ZoneId.systemDefault()).toLocalDate();
                    FocusSession session = new FocusSession(
                            rs.getInt("session_id"),
                            userId,
                            sessionDate,
                            rs.getInt("work_time"),
                            rs.getInt("break_time")
                    );
                    sessions.add(session);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving focus sessions: " + ex.getMessage());
        }
        return sessions;
    }

    public String getDayStreak(int userId) {
        String query = "SELECT day_streak FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("day_streak") + " Days";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 Days";
    }

    public int getTotalWorkTime(int userId) {
        String query = "SELECT SUM(work_time) AS total_work_time FROM focus_sessions WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total_work_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalBreakTime(int userId) {
        String query = "SELECT SUM(break_time) AS total_break_time FROM focus_sessions WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total_break_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getDaysAccessed(int userId) {
        String query = "SELECT COUNT(DISTINCT session_date) AS days_accessed FROM focus_sessions WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("days_accessed") + " Days";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 Days";
    }
}
