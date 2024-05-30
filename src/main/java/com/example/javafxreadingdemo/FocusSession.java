package com.example.javafxreadingdemo;

import java.time.LocalDate;

public class FocusSession {
    private int sessionId;
    private int userId;
    private LocalDate sessionDate;
    private int workTime; // in seconds
    private int breakTime; // in seconds

    public FocusSession(int sessionId, int userId, LocalDate sessionDate, int workTime, int breakTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.sessionDate = sessionDate;
        this.workTime = workTime;
        this.breakTime = breakTime;
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

    public int getWorkTime() {
        return workTime;
    }

    public int getBreakTime() {
        return breakTime;
    }

    @Override
    public String toString() {
        return "FocusSession{" +
                "sessionId=" + sessionId +
                ", userId=" + userId +
                ", sessionDate=" + sessionDate +
                ", workTime=" + workTime +
                ", breakTime=" + breakTime +
                '}';
    }
}
