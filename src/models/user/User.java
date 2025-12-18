/**
 * @author Javanshir Aghayev
 */

package models.user;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private List<Integer> enrolledCourseIds;
    private List<Integer> friendIds;
    private List<Badge> badges;

    public User(int userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.xpAmount = 0;
        this.currentStreak = 0;
        this.lastLoginDate = null;
        this.registrationDate = LocalDate.now();
        this.enrolledCourseIds = new ArrayList<>();
        this.friendIds = new ArrayList<>();
        this.badges = new ArrayList<>();
        this.profilePicture = "default.png";
    }

    public boolean login(String inputPassword) {
        if (this.password.equals(inputPassword)) {
            calculateStreak();
            this.lastLoginDate = LocalDate.now();
            System.out.println("Login successful for user: " + username);
            return true;
        }
        System.out.println("Login failed: Incorrect password");
        return false;
    }

    public static User register(String username, String email, String password) {
        int newUserId = generateUserId();
        User newUser = new User(newUserId, username, email, password);
        System.out.println("User registered successfully: " + username);
        return newUser;
    }

    public void updateProfile(String newUsername, String newEmail, String newProfilePicture) {
        if (newUsername != null && !newUsername.isEmpty()) {
            this.username = newUsername;
        }
        if (newEmail != null && !newEmail.isEmpty()) {
            this.email = newEmail;
        }
        if (newProfilePicture != null && !newProfilePicture.isEmpty()) {
            this.profilePicture = newProfilePicture;
        }
        System.out.println("Profile updated successfully");
    }

    public boolean enrollCourse(int courseId) {
        if (enrolledCourseIds.contains(courseId)) {
            System.out.println("Already enrolled in this course");
            return false;
        }
        enrolledCourseIds.add(courseId);
        System.out.println("Enrolled in course ID: " + courseId);
        return true;
    }

    public boolean addFriend(int friendId) {
        if (friendId == this.userId) {
            System.out.println("Cannot add yourself as a friend");
            return false;
        }
        if (friendIds.contains(friendId)) {
            System.out.println("Already friends with user ID: " + friendId);
            return false;
        }
        friendIds.add(friendId);
        System.out.println("Added friend ID: " + friendId);
        return true;
    }

    public boolean removeFriend(int friendId) {
        if (friendIds.remove(Integer.valueOf(friendId))) {
            System.out.println("Removed friend ID: " + friendId);
            return true;
        }
        System.out.println("Friend ID not found: " + friendId);
        return false;
    }

    public void updateXPAmount(int xpToAdd) {
        if (xpToAdd < 0) {
            System.out.println("Cannot add negative XP");
            return;
        }
        this.xpAmount += xpToAdd;
        System.out.println("XP updated. New XP: " + this.xpAmount);
    }

    public void calculateStreak() {
        LocalDate today = LocalDate.now();

        if (lastLoginDate == null) {
            currentStreak = 1;
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastLoginDate, today);

            if (daysBetween == 0) {
                System.out.println("Already logged in today. Streak: " + currentStreak);
            } else if (daysBetween == 1) {
                currentStreak++;
                System.out.println("Streak increased to: " + currentStreak);
            } else {
                currentStreak = 1;
                System.out.println("Streak broken. Reset to: " + currentStreak);
            }
        }
    }

    public void addBadge(Badge badge) {
        if (!badges.contains(badge)) {
            badges.add(badge);
            System.out.println("Badge earned: " + badge.getName());
        }
    }

    private static int generateUserId() {
        return (int) (Math.random() * 100000);
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public int getXpAmount() {
        return xpAmount;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public List<Integer> getEnrolledCourseIds() {
        return new ArrayList<>(enrolledCourseIds);
    }

    public List<Integer> getFriendIds() {
        return new ArrayList<>(friendIds);
    }

    public List<Badge> getBadges() {
        return new ArrayList<>(badges);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", xpAmount=" + xpAmount +
                ", currentStreak=" + currentStreak +
                '}';
    }
}