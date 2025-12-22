package com.edutrack.model;

public class Task {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String dueDate;
    private String status; // PENDING, COMPLETED, OVERDUE
    private String courseTag;

    public Task(int id, int userId, String title, String description, String dueDate, String status, String courseTag) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.courseTag = courseTag;
    }

    public Task(int userId, String title, String description, String dueDate, String courseTag) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = "PENDING";
        this.courseTag = courseTag;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCourseTag() {
        return courseTag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Due: %s)", status, title, dueDate);
    }
}
