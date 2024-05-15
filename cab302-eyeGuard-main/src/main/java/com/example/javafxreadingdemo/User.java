package com.example.javafxreadingdemo;

public class User {
    private int id;  // This can be useful if you're planning to uniquely identify users.
    private String email;
    private String password;

    // Constructor used when creating a user when you don't have an ID yet (e.g., before inserting into the database)
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor used when retrieving user data from the database
    public User(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='*****'" +  // Masking password for security in logs
                '}';
    }
}
