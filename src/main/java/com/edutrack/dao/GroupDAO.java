package com.edutrack.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.edutrack.controller.FriendsController;
import com.edutrack.model.User;
import com.edutrack.util.DatabaseManager;

public class GroupDAO {

    public boolean createGroup(String name, int ownerId) {
        String sql = "INSERT INTO study_groups(name, owner_id) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, ownerId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Add owner as a member automatically
                GroupRecord group = getGroupByName(name);
                if (group != null) {
                    joinGroup(group.id, ownerId);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean joinGroup(int groupId, int userId) {
        String sql = "INSERT INTO group_members(group_id, user_id) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace(); // Likely already member or error
        }
        return false;
    }

    public boolean leaveGroup(int groupId, int userId) {
        String sql = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            // Check if group is empty, if so delete it
            if (getMemberCount(groupId) == 0) {
                deleteGroup(groupId);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteGroup(int groupId) {
        String sqlMembers = "DELETE FROM group_members WHERE group_id = ?";
        String sqlGroup = "DELETE FROM study_groups WHERE id = ?";
        try (Connection conn = DatabaseManager.connect()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlMembers)) {
                pstmt.setInt(1, groupId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sqlGroup)) {
                pstmt.setInt(1, groupId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GroupRecord getGroupByName(String name) {
        String sql = "SELECT * FROM study_groups WHERE name = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new GroupRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("owner_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GroupRecord getGroupById(int id) {
        String sql = "SELECT * FROM study_groups WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new GroupRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("owner_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GroupRecord getUserGroup(int userId) {
        // Find which group the user belongs to
        String sql = "SELECT g.* FROM study_groups g JOIN group_members gm ON g.id = gm.group_id WHERE gm.user_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new GroupRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("owner_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FriendsController.User> getGroupMembers(int groupId) {
        List<FriendsController.User> members = new ArrayList<>();
        String sql = "SELECT u.*, gm.is_ready FROM users u JOIN group_members gm ON u.id = gm.user_id WHERE gm.group_id = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String avatar = rs.getString("profile_picture");
                if (avatar == null || avatar.isEmpty())
                    avatar = "/com/edutrack/view/avatar1.png";

                FriendsController.User user = new FriendsController.User(
                        rs.getString("username"),
                        avatar,
                        rs.getInt("points") / 100 + 1 // Calc level roughly if not stored
                );
                user.setReady(rs.getBoolean("is_ready"));
                members.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public int getMemberCount(int groupId) {
        String sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setMemberReady(int groupId, int userId, boolean isReady) {
        String sql = "UPDATE group_members SET is_ready = ? WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isReady);
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class GroupRecord {
        public int id;
        public String name;
        public int ownerId;

        public GroupRecord(int id, String name, int ownerId) {
            this.id = id;
            this.name = name;
            this.ownerId = ownerId;
        }
    }
}
