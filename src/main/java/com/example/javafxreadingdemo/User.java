package com.example.javafxreadingdemo;

import java.time.LocalDate;

public class User {
    private int id; // This can be useful if you're planning to uniquely identify users.
    private String email;
    private String password;
    private LocalDate lastAccessDate; // Added to track the last date of access.
    private int dayStreak; // Added to track the number of consecutive days accessed.

    // Constructor used when creating a user when you don't have an ID yet (e.g., before inserting into the database)
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor used when retrieving user data from the database, including new fields
    public User(int id, String email, String password, LocalDate lastAccessDate, int dayStreak) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.lastAccessDate = lastAccessDate;
        this.dayStreak = dayStreak;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getLastAccessDate() {
        return lastAccessDate;
    }

    public int getDayStreak() {
        return dayStreak;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastAccessDate(LocalDate lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public void setDayStreak(int dayStreak) {
        this.dayStreak = dayStreak;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='*****', " +  // Masking password for security in logs
                ", lastAccessDate=" + lastAccessDate +
                ", dayStreak=" + dayStreak +
                '}';
    }
}
