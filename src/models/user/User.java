package models.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String profilePicture;
    private int xpAmount;
    private int currentStreak;
    private LocalDate lastLoginDate;
    private LocalDate registrationDate;

    private List<Integer> enrolledCourseIds = new ArrayList<>();
    private List<Integer> friendIds = new ArrayList<>();

    public User(int userId, String username, String email, String password, 
                int xpAmount, int currentStreak, String profilePicture) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.xpAmount = xpAmount;
        this.currentStreak = currentStreak;
        this.profilePicture = profilePicture != null ? profilePicture : "default.png";
        this.registrationDate = LocalDate.now();
    }

    public User(int userId, String username, String email, String password) {
        this(userId, username, email, password, 0, 0, "default.png");
    }

    public void updateXPAmount(int xpToAdd) {
        if (xpToAdd >= 0) {
            this.xpAmount += xpToAdd;
        }
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getXpAmount() {
        return xpAmount;
    }

    public void setXpAmount(int xpAmount) {
        this.xpAmount = xpAmount;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<Integer> getEnrolledCourseIds() {
        return enrolledCourseIds;
    }

    public void setEnrolledCourseIds(List<Integer> enrolledCourseIds) {
        this.enrolledCourseIds = enrolledCourseIds;
    }

    public List<Integer> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(List<Integer> friendIds) {
        this.friendIds = friendIds;
    }

    
}