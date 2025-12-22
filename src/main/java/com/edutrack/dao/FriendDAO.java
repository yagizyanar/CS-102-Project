package com.edutrack.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.edutrack.model.UserRequest;
import com.edutrack.util.DatabaseManager;

public class FriendDAO {

    public boolean sendRequest(int requesterId, int addresseeId) {
        String sql = "INSERT INTO friendships (requester_id, addressee_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, addresseeId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("Friend request already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void acceptRequest(int requesterId, int addresseeId) {
        String sql = "UPDATE friendships SET status = 'ACCEPTED' WHERE requester_id = ? AND addressee_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, addresseeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFriendship(int requesterId, int addresseeId) {
        String sql = "DELETE FROM friendships WHERE (requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requesterId);
            pstmt.setInt(2, addresseeId);
            pstmt.setInt(3, addresseeId);
            pstmt.setInt(4, requesterId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UserRequest> searchUsers(String query, int currentUserId) {
        List<UserRequest> users = new ArrayList<>();

        String sql = "SELECT u.id, u.username, " +
                "f.status, f.requester_id " +
                "FROM users u " +
                "LEFT JOIN friendships f ON " +
                "  (f.requester_id = ? AND f.addressee_id = u.id) OR " +
                "  (f.requester_id = u.id AND f.addressee_id = ?) " +
                "WHERE u.username LIKE ? AND u.id != ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);
            pstmt.setString(3, query + "%");
            pstmt.setInt(4, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String rawStatus = rs.getString("status");
                int requester = rs.getInt("requester_id");

                String derivedStatus = "NONE";

                if (rawStatus != null) {
                    if ("ACCEPTED".equals(rawStatus)) {
                        derivedStatus = "FRIEND";
                    } else if ("PENDING".equals(rawStatus)) {
                        if (requester == currentUserId) {
                            derivedStatus = "SENT";
                        } else {
                            derivedStatus = "RECEIVED";
                        }
                    }
                }

                users.add(new UserRequest(
                        rs.getInt("id"),
                        rs.getString("username"),
                        derivedStatus));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<UserRequest> getPendingRequests(int userId) {
        List<UserRequest> requests = new ArrayList<>();
        String sql = "SELECT u.id, u.username, 'PENDING' as status " +
                "FROM friendships f " +
                "JOIN users u ON f.requester_id = u.id " +
                "WHERE f.addressee_id = ? AND f.status = 'PENDING'";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(new UserRequest(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "PENDING"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<UserRequest> getFriends(int userId) {
        List<UserRequest> friends = new ArrayList<>();
        String sql = "SELECT u.id, u.username, 'ACCEPTED' as status " +
                "FROM friendships f " +
                "JOIN users u ON (f.requester_id = u.id OR f.addressee_id = u.id) " +
                "WHERE (f.requester_id = ? OR f.addressee_id = ?) " +
                "AND f.status = 'ACCEPTED' AND u.id != ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                friends.add(new UserRequest(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "ACCEPTED"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
