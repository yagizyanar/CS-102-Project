package com.edutrack.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.edutrack.model.User;
import com.edutrack.util.DatabaseManager;

public class UserDAO {

    public boolean registerUser(User user) {
        // Enforce Unique Password Policy
        if (isPasswordTaken(user.getPassword())) {
            System.out.println("Registration Failed: Password matches an existing user (Identity Policy).");
            return false;
        }

        String sql = "INSERT INTO users(username, password, email, major, profile_picture) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getMajor());
            pstmt.setString(5, user.getProfilePicture());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("major"));
                // Load additional profile fields
                try {
                    user.setUniversity(rs.getString("university"));
                    user.setBio(rs.getString("bio"));
                    user.setNotes(rs.getString("notes"));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    user.setXpAmount(rs.getInt("points")); // Load points
                    String classesStr = rs.getString("classes");
                    if (classesStr != null && !classesStr.isBlank()) {
                        for (String c : classesStr.split(",")) {
                            user.addClass(c.trim());
                        }
                    }
                } catch (SQLException e) {
                    // Columns might not exist yet
                }
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User loginUserByEmail(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("major"));

                try {
                    user.setUniversity(rs.getString("university"));
                    user.setBio(rs.getString("bio"));
                    user.setNotes(rs.getString("notes"));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    user.setXpAmount(rs.getInt("points"));
                    String classesStr = rs.getString("classes");
                    if (classesStr != null && !classesStr.isBlank()) {
                        for (String c : classesStr.split(",")) {
                            user.addClass(c.trim());
                        }
                    }
                } catch (SQLException e) {

                }
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET university = ?, bio = ?, notes = ?, profile_picture = ?, major = ?, classes = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUniversity());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getNotes());
            pstmt.setString(4, user.getProfilePicture());
            pstmt.setString(5, user.getMajor());
            pstmt.setString(6, user.getClassesText());
            pstmt.setInt(7, user.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    // New method for gamification
    public boolean addPoints(int userId, int points) {
        String sql = "UPDATE users SET points = points + ? WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding points: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("major"));
                try {
                    user.setUniversity(rs.getString("university"));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    String classesStr = rs.getString("classes");
                    if (classesStr != null && !classesStr.isBlank()) {
                        for (String c : classesStr.split(",")) {
                            user.addClass(c.trim());
                        }
                    }
                } catch (SQLException e) {
                    // Columns might not exist yet
                }
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("major"));
                try {
                    user.setUniversity(rs.getString("university"));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    String classesStr = rs.getString("classes");
                    if (classesStr != null && !classesStr.isBlank()) {
                        for (String c : classesStr.split(",")) {
                            user.addClass(c.trim());
                        }
                    }
                } catch (SQLException e) {
                    // Columns might not exist yet
                }
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("major"));
                try {
                    user.setUniversity(rs.getString("university"));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    String classesStr = rs.getString("classes");
                    if (classesStr != null && !classesStr.isBlank()) {
                        for (String c : classesStr.split(",")) {
                            user.addClass(c.trim());
                        }
                    }
                } catch (SQLException e) {
                    // Columns might not exist yet
                }
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private boolean isPasswordTaken(String password) {
        String sql = "SELECT id FROM users WHERE password = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a record exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Fail safe
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        // Enforce Unique Password Policy (if you want to keep it consistent with
        // registration)
        if (isPasswordTaken(newPassword)) {
            System.out.println("Password Update Failed: Password matches an existing user (Identity Policy).");
            return false;
        }

        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
}
