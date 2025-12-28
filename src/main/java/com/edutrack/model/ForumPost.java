package com.edutrack.model;

public class ForumPost {
    private int id;
    private String courseCode;
    private String username;
    private String content;
    private String timestamp;

    public ForumPost(int id, String courseCode, String username, String content, String timestamp) {
        this.id = id;
        this.courseCode = courseCode;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ForumPost(String courseCode, String username, String content) {
        this.courseCode = courseCode;
        this.username = username;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return username + ": " + content + " (" + timestamp + ")";
    }
}
