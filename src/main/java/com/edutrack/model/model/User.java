package com.edutrack.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String major;

    private int xpAmount = 0;
    private int currentStreak = 0;
    private static final int XP_PER_LEVEL = 500;
    private java.util.List<Badge> badges = new java.util.ArrayList<>();

    public void addBadge(Badge badge) {
        if (badge != null && !badges.contains(badge)) {
            badges.add(badge);
        }
    }

    public java.util.List<Badge> getBadges() {
        return new java.util.ArrayList<>(badges);
    }

    public User(int id, String username, String password, String email, String major) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.major = major;
    }

    public User(String username, String password, String email, String major) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.major = major;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    // --- New Features (XP/Streak) ---
    public int getXpAmount() {
        return xpAmount;
    }

    public void setXpAmount(int xpAmount) {
        this.xpAmount = xpAmount;
    }

    // Alias for compatibility with ProfileController
    public int getXp() {
        return xpAmount;
    }

    public int getNextLevelXp() {
        return getLevel() * XP_PER_LEVEL;
    }

    public int calculateStreak() {
        return currentStreak;
    }

    public int getLevel() {
        return (xpAmount / XP_PER_LEVEL) + 1;
    }

    // --- Profile Fields ---
    private String university = "";
    private String bio = "";
    private String notes = "";
    private String profilePicture = "/com/edutrack/view/avatar1.png";
    private final java.util.List<String> classesList = new java.util.ArrayList<>();

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        if (university != null)
            this.university = university;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        if (bio != null)
            this.bio = bio;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes != null)
            this.notes = notes;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        if (profilePicture != null && !profilePicture.isBlank())
            this.profilePicture = profilePicture;
    }

    public java.util.List<String> getClassesList() {
        return java.util.Collections.unmodifiableList(classesList);
    }

    public String getClassesText() {
        return String.join(", ", classesList);
    }

    public void addClass(String courseCode) {
        if (courseCode != null && !courseCode.trim().isEmpty() && !classesList.contains(courseCode.trim())) {
            classesList.add(courseCode.trim());
        }
    }

    public void removeClass(String courseCode) {
        if (courseCode != null)
            classesList.remove(courseCode.trim());
    }
}
