package com.example.autodo;

public class TaskData {
    private String googleId;
    private String dateTimeCreated;
    private String name;
    private String details;
    private String urgency;
    private String dateTimeDeadline;
    private boolean status;

    public TaskData(String googleId, String dateTimeCreated, String name, String details, String urgency, String dateTimeDeadline, boolean status) {
        this.googleId = googleId;
        this.dateTimeCreated = dateTimeCreated;
        this.name = name;
        this.details = details;
        this.urgency = urgency;
        this.dateTimeDeadline = dateTimeDeadline;
        this.status = status;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getDateTimeCreated() {
        return dateTimeCreated;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public String getUrgency() {
        return urgency;
    }

    public String getDateTimeDeadline() {
        return dateTimeDeadline;
    }

    public boolean isStatus() {
        return status;
    }
}
