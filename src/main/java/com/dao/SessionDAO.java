package com.edutrack.dao;

import com.edutrack.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public void logSession(int userId, int durationMinutes, String type) {
        String sql = "INSERT INTO sessions(user_id, start_time, duration_minutes, type) VALUES(?, datetime('now'), ?, ?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, durationMinutes);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Returns List of Object[] {Username, TotalMinutes}
    public List<Object[]> getLeaderboardData() {
        List<Object[]> leaderboard = new ArrayList<>();
        String sql = "SELECT u.username, SUM(s.duration_minutes) as total_time " +
                "FROM sessions s " +
                "JOIN users u ON s.user_id = u.id " +
                "GROUP BY u.username " +
                "ORDER BY total_time DESC";

        try (Connection conn = DatabaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                leaderboard.add(new Object[] { rs.getString("username"), rs.getInt("total_time") });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return leaderboard;
    }
}
