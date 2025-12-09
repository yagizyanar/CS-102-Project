package src.courses;

import java.util.ArrayList;
import java.util.List;

import courses.ForumPost;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private ArrayList<ForumPost> forumPosts;
    private ArrayList<User> enrolledUsers;

    public Course(int courseId, String courseCode, String courseName) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.forumPosts = new ArrayList<>();
        this.enrolledUsers = new ArrayList<>();
    }

    public void addForumPost(ForumPost post) {
        this.forumPosts.add(post);
    }

    public void removeForumPost(int postId) {
        int indexToRemove = -1;
        for(int i = 0; i < forumPosts.size(); i++){
            if(forumPosts.get(i).getPostId() == postId){
                indexToRemove = i;
            }
        }
        if(indexToRemove != -1){
            forumPosts.remove(indexToRemove);
        }
    }

    public void enrollUser(User user) {
        if (user != null && !this.enrolledUsers.contains(user)) {
            this.enrolledUsers.add(user);
        }
    }

    public ArrayList<User> getEnrolledUsers() {
        return enrolledUsers;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(ArrayList<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }
}
