package com.edutrack.dao;

import com.edutrack.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    public static class GoalRecord {
        public int id;
        public int userId;
        public String name;
        public String deadline;
        public boolean completed;

        public GoalRecord(int id, int userId, String name, String deadline, boolean completed) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.deadline = deadline;
            this.completed = completed;
        }
    }

    public boolean addGoal(int userId, String name, String deadline) {
        String sql = "INSERT INTO goals(user_id, name, deadline, completed) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, deadline);
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding goal: " + e.getMessage());
            return false;
        }
    }

    public List<GoalRecord> getGoalsByUserId(int userId) {
        List<GoalRecord> goals = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                goals.add(new GoalRecord(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("deadline"),
                        rs.getBoolean("completed")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting goals: " + e.getMessage());
        }
        return goals;
    }

    public void updateGoalCompleted(int goalId, boolean completed) {
        String sql = "UPDATE goals SET completed = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, completed);
            pstmt.setInt(2, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating goal: " + e.getMessage());
        }
    }

    public void deleteGoal(int goalId) {
        String sql = "DELETE FROM goals WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting goal: " + e.getMessage());
        }
    }
}
