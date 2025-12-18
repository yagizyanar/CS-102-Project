package model;

import java.util.ArrayList;

public class Course {
    private int courseId;
    private String courseCode; 
    private String courseName; 
    
    
    private ArrayList<ForumPost> forumPosts;

    public Course() {
        this.forumPosts = new ArrayList<>();
    }

    public Course(int courseId, String courseCode, String courseName) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.forumPosts = new ArrayList<>();
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public ArrayList<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void addForumPost(ForumPost post) {
        if (this.forumPosts == null) {
            this.forumPosts = new ArrayList<>();
        }
        this.forumPosts.add(post);
    }

    public void removeForumPost(int postId) {
        if (this.forumPosts != null) {
            this.forumPosts.removeIf(p -> p.getPostId() == postId);
        }
    }

    public ArrayList<User> getEnrolledUsers() {
        return new ArrayList<>(); 
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
