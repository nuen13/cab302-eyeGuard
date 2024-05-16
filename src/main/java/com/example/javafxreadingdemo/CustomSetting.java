package com.example.javafxreadingdemo;

public class CustomSetting {
    private int userId;
    private String themeColor;
    private String soundAlert;
    private int breakTime;
    private int workTime;

    // Constructor
    public CustomSetting(int userId, String themeColor, String soundAlert, int breakTime, int workTime) {
        this.userId = userId;
        this.themeColor = themeColor;
        this.soundAlert = soundAlert;
        this.breakTime = breakTime;
        this.workTime = workTime;
    }

    // Getter method for themeColor
    public String getThemeColor() {
        return themeColor;
    }

    // Getter method for soundAlert
    public String getSoundAlert() {
        return soundAlert;
    }

    // Getter method for breakTime
    public int getBreakTime() {
        return breakTime;
    }

    // Getter method for workTime
    public int getWorkTime() {
        return workTime;
    }
}