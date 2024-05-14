package com.example.javafxreadingdemo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        connection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() {
        createTable();
        createFocusSessionTable();
    }

    public void createTable() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table if it doesn't exist
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "email VARCHAR(255) NOT NULL UNIQUE, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "last_access_date DATE, " +
                            "day_streak INTEGER DEFAULT 0" +
                            ")"
            );

            // Add columns if they do not exist
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)");
            boolean lastAccessDateExists = false;
            boolean dayStreakExists = false;

            while (rs.next()) {
                if ("last_access_date".equals(rs.getString("name"))) {
                    lastAccessDateExists = true;
                }
                if ("day_streak".equals(rs.getString("name"))) {
                    dayStreakExists = true;
                }
            }

            if (!lastAccessDateExists) {
                stmt.execute("ALTER TABLE users ADD COLUMN last_access_date DATE");
            }
            if (!dayStreakExists) {
                stmt.execute("ALTER TABLE users ADD COLUMN day_streak INTEGER DEFAULT 0");
            }

        } catch (SQLException ex) {
            System.err.println("Error while creating or modifying the users table: " + ex.getMessage());
        }
    }

    public void createFocusSessionTable() {
        try (Statement stmt = connection.createStatement()) {
            // Create the focus_sessions table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS focus_sessions (" +
                            "session_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "user_id INTEGER, " +
                            "session_date DATE, " +
                            "focus_duration INTEGER, " +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
        } catch (SQLException ex) {
            System.err.println("Error while creating focus_sessions table: " + ex.getMessage());
        }
    }

    public void insert(User user) {
        try (PreparedStatement insertUser = connection.prepareStatement(
                "INSERT INTO users (email, password, last_access_date, day_streak) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            insertUser.setString(1, user.getEmail());
            insertUser.setString(2, user.getPassword());
            insertUser.setDate(3, Date.valueOf(LocalDate.now()));  // Assuming the user is accessed on creation
            insertUser.setInt(4, 1);  // Starts with the first access day
            insertUser.execute();

            // Set the generated ID to the user object
            try (ResultSet generatedKeys = insertUser.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error inserting user: " + ex.getMessage());
        }
    }

    public void update(User user) {
        try (PreparedStatement updateUser = connection.prepareStatement(
                "UPDATE users SET email = ?, password = ?, last_access_date = ?, day_streak = ? WHERE id = ?")) {
            updateUser.setString(1, user.getEmail());
            updateUser.setString(2, user.getPassword());
            updateUser.setDate(3, Date.valueOf(LocalDate.now()));  // Optionally update last access date on update
            updateUser.setInt(4, user.getDayStreak());  // Update streak if necessary
            updateUser.setInt(5, user.getId());
            updateUser.execute();
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    public void delete(int id) {
        try (PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            deleteUser.setInt(1, id);
            deleteUser.execute();
        } catch (SQLException ex) {
            System.err.println("Error deleting user: " + ex.getMessage());
        }
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Statement getAll = connection.createStatement();
             ResultSet rs = getAll.executeQuery("SELECT id, email, password, last_access_date, day_streak FROM users")) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getDate("last_access_date").toLocalDate(),
                        rs.getInt("day_streak")
                ));
            }
        } catch (SQLException ex) {
            System.err.println("Error getting all users: " + ex.getMessage());
        }
        return users;
    }

    public User getById(int id) {
        try (PreparedStatement getUser = connection.prepareStatement(
                "SELECT id, email, password, last_access_date, day_streak FROM users WHERE id = ?")) {
            getUser.setInt(1, id);
            try (ResultSet rs = getUser.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("last_access_date").toLocalDate(),
                            rs.getInt("day_streak")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error getting user by ID: " + ex.getMessage());
        }
        return null;
    }

    public User getUserByEmail(String email) {
        try (PreparedStatement getUser = connection.prepareStatement(
                "SELECT id, email, password, last_access_date, day_streak FROM users WHERE email = ?")) {
            getUser.setString(1, email);
            try (ResultSet rs = getUser.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("last_access_date").toLocalDate(),
                            rs.getInt("day_streak")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error getting user by email: " + ex.getMessage());
        }
        return null;
    }

    public Integer validateUser(String email, String password) {
        try (PreparedStatement validateUser = connection.prepareStatement(
                "SELECT id FROM users WHERE email = ? AND password = ?")) {
            validateUser.setString(1, email);
            validateUser.setString(2, password);
            try (ResultSet rs = validateUser.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error validating user: " + ex.getMessage());
        }
        return null;
    }

    public void insertFocusSession(int userId, LocalDate sessionDate, int duration) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO focus_sessions (user_id, session_date, focus_duration) VALUES (?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(sessionDate));
            statement.setInt(3, duration);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error inserting focus session: " + ex.getMessage());
        }
    }

    public List<FocusSession> getFocusSessionsByUserId(int userId) {
        List<FocusSession> sessions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT session_id, user_id, session_date, focus_duration FROM focus_sessions WHERE user_id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    FocusSession session = new FocusSession(
                            rs.getInt("session_id"),
                            userId,
                            rs.getDate("session_date").toLocalDate(),
                            rs.getInt("focus_duration")
                    );
                    sessions.add(session);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving focus sessions: " + ex.getMessage());
        }
        return sessions;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Error closing connection: " + ex.getMessage());
        }
    }
}
