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
                try {
                    user.setUniversity(rs.getString("university"));
                    user.setBio(rs.getString("bio"));
                    user.setNotes(rs.getString("notes"));
                    user.setProfilePicture(rs.getString("profile_picture"));
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
        String sql = "UPDATE users SET university = ?, bio = ?, notes = ?, profile_picture = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUniversity());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getNotes());
            pstmt.setString(4, user.getProfilePicture());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
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
                } catch (SQLException e) {
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
                } catch (SQLException e) {
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
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
}
