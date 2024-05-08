package com.example.javafxreadingdemo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        connection = DatabaseConnection.getInstance();
    }

    public void createTable() {
        try {
            Statement createTable = connection.createStatement();
            createTable.execute(
                    "CREATE TABLE IF NOT EXISTS users ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "email VARCHAR(255) NOT NULL UNIQUE, "
                            + "password VARCHAR(255) NOT NULL"
                            + ")"
            ); // Columns for tracking access and streak
            createTable.execute(
                    "ALTER TABLE users ADD COLUMN last_access_date DATE"
            );
            createTable.execute(
                    "ALTER TABLE users ADD COLUMN day_streak INTEGER DEFAULT 0"
            );
        } catch (SQLException ex) {
            if (ex.getMessage().contains("duplicate column name")){
                System.out.println("Column already exists, will not add again.");
            }else {
                System.err.println(ex);
            }
        }
    }

    public void insert(User user) {
        try {
            PreparedStatement insertUser = connection.prepareStatement(
                    "INSERT INTO users (email, password) VALUES (?, ?)"
            );
            insertUser.setString(1, user.getEmail());
            insertUser.setString(2, user.getPassword());  // Consider hashing the password before storing
            insertUser.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    public void update(User user) {
        try {
            PreparedStatement updateUser = connection.prepareStatement(
                    "UPDATE users SET email = ?, password = ? WHERE id = ?"
            );
            updateUser.setString(1, user.getEmail());
            updateUser.setString(2, user.getPassword());  // Consider re-hashing the password if it's new
            updateUser.setInt(3, user.getId());
            updateUser.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    public void delete(int id) {
        try {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            deleteUser.setInt(1, id);
            deleteUser.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet rs = getAll.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                users.add(
                        new User(
                                rs.getInt("id"),
                                rs.getString("email"),
                                rs.getString("password")
                        )
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return users;
    }

    public User getById(int id) {
        try {
            PreparedStatement getUser = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            getUser.setInt(1, id);
            ResultSet rs = getUser.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    public void updateLoginDateAndStreak(int userId){
        try {
            PreparedStatement getLastAccess = connection.prepareStatement(
                    "SELECT last_access_date, day_streak FROM users WHERE id = ?"
            );
            getLastAccess.setInt(1, userId);
            ResultSet rs = getLastAccess.executeQuery();
            LocalDate lastAccessDate = null;
            int dayStreak = 0;
            if (rs.next()){
                lastAccessDate = rs.getDate("last_access_date").toLocalDate();
                dayStreak = rs.getInt("day_streak");
            }
            LocalDate today = LocalDate.now();
            int newStreak = (lastAccessDate != null && lastAccessDate.equals(today.minusDays(1))) ? dayStreak + 1 : 1;

            //Update the last access date and day streak
            PreparedStatement updateAccess = connection.prepareStatement(
                    "UPDATE users SET last_access_date = ?, day_streak = ? WHERE id = ?");
            updateAccess.setDate(1, Date.valueOf(today));
            updateAccess.setInt(2, newStreak);
            updateAccess.setInt(3, userId);
            updateAccess.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
}
