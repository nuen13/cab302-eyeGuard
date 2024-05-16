package com.example.javafxreadingdemo;

import java.time.LocalDate;

public class FocusSession {
    private int sessionId;
    private int userId;
    private LocalDate sessionDate;
    private int focusDuration; // in minutes

    public FocusSession(int sessionId, int userId, LocalDate sessionDate, int focusDuration) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.sessionDate = sessionDate;
        this.focusDuration = focusDuration;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public int getFocusDuration() {
        return focusDuration;
    }

    @Override
    public String toString() {
        return "FocusSession{" +
                "sessionId=" + sessionId +
                ", userId=" + userId +
                ", sessionDate=" + sessionDate +
                ", focusDuration=" + focusDuration +
                '}';
    }
}
