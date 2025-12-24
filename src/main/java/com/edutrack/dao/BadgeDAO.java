package com.edutrack.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.edutrack.model.Badge;
import com.edutrack.util.DatabaseManager;

public class BadgeDAO {

    public void initializeTables() {
        String badgesTable = "CREATE TABLE IF NOT EXISTS badges ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "icon_path TEXT,"
                + "category TEXT,"
                + "required_value INTEGER DEFAULT 0"
                + ");";

        String userBadgesTable = "CREATE TABLE IF NOT EXISTS user_badges ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "badge_id INTEGER NOT NULL,"
                + "earned_at TEXT DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id),"
                + "FOREIGN KEY (badge_id) REFERENCES badges(id),"
                + "UNIQUE(user_id, badge_id)"
                + ");";

        try (Connection conn = DatabaseManager.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(badgesTable);
            stmt.execute(userBadgesTable);
            
            // Insert default badges if not exist
            insertDefaultBadges(conn);
        } catch (SQLException e) {
            System.out.println("BadgeDAO init error: " + e.getMessage());
        }
    }

    private void insertDefaultBadges(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM badges";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Badges already exist
            }
        }

        String insertSql = "INSERT INTO badges(name, description, icon_path, category, required_value) VALUES(?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            // Streak badges
            addBadge(pstmt, "First Steps", "Complete your first day!", "com/edutrack/view/streak.png", "STREAK", 1);
            addBadge(pstmt, "Week Warrior", "7 day streak!", "com/edutrack/view/streak.png", "STREAK", 7);
            addBadge(pstmt, "Dedicated", "14 day streak!", "com/edutrack/view/streak.png", "STREAK", 14);
            addBadge(pstmt, "Monthly Master", "30 day streak!", "com/edutrack/view/streak.png", "STREAK", 30);

            // XP badges
            addBadge(pstmt, "Beginner", "Earn 50 XP", "com/edutrack/view/avatar1.png", "XP", 50);
            addBadge(pstmt, "Learner", "Earn 100 XP", "com/edutrack/view/avatar2.png", "XP", 100);
            addBadge(pstmt, "Scholar", "Earn 500 XP", "com/edutrack/view/avatar3.png", "XP", 500);
            addBadge(pstmt, "Expert", "Earn 1000 XP", "com/edutrack/view/avatar4.png", "XP", 1000);

            // Task badges
            addBadge(pstmt, "Task Starter", "Complete 1 task", "com/edutrack/view/task.png", "PRODUCTIVITY", 1);
            addBadge(pstmt, "Productive", "Complete 10 tasks", "com/edutrack/view/task.png", "PRODUCTIVITY", 10);
            addBadge(pstmt, "Task Master", "Complete 50 tasks", "com/edutrack/view/task.png", "PRODUCTIVITY", 50);

            // Social badges
            addBadge(pstmt, "Friendly", "Add your first friend", "com/edutrack/view/friends.png", "SOCIAL", 1);
            addBadge(pstmt, "Popular", "Have 5 friends", "com/edutrack/view/friends.png", "SOCIAL", 5);

            // Pomodoro badges
            addBadge(pstmt, "Focus Beginner", "Complete 1 Pomodoro session", "com/edutrack/view/pomodoro.png", "PRODUCTIVITY", 1);
            addBadge(pstmt, "Focus Pro", "Complete 10 Pomodoro sessions", "com/edutrack/view/pomodoro.png", "PRODUCTIVITY", 10);
        }
    }

    private void addBadge(PreparedStatement pstmt, String name, String desc, String icon, String category, int value) throws SQLException {
        pstmt.setString(1, name);
        pstmt.setString(2, desc);
        pstmt.setString(3, icon);
        pstmt.setString(4, category);
        pstmt.setInt(5, value);
        pstmt.executeUpdate();
    }

    public List<Badge> getAllBadges() {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT * FROM badges";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Badge badge = new Badge(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("icon_path"),
                        Badge.BadgeCategory.valueOf(rs.getString("category")),
                        rs.getInt("required_value"));
                badges.add(badge);
            }
        } catch (SQLException e) {
            System.out.println("getAllBadges error: " + e.getMessage());
        }
        return badges;
    }

    public List<Badge> getUserBadges(int userId) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT b.* FROM badges b " +
                "INNER JOIN user_badges ub ON b.id = ub.badge_id " +
                "WHERE ub.user_id = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Badge badge = new Badge(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("icon_path"),
                        Badge.BadgeCategory.valueOf(rs.getString("category")),
                        rs.getInt("required_value"));
                badges.add(badge);
            }
        } catch (SQLException e) {
            System.out.println("getUserBadges error: " + e.getMessage());
        }
        return badges;
    }

    public boolean awardBadge(int userId, int badgeId) {
        String sql = "INSERT OR IGNORE INTO user_badges(user_id, badge_id) VALUES(?,?)";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, badgeId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("awardBadge error: " + e.getMessage());
            return false;
        }
    }

    public boolean userHasBadge(int userId, int badgeId) {
        String sql = "SELECT 1 FROM user_badges WHERE user_id = ? AND badge_id = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, badgeId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("userHasBadge error: " + e.getMessage());
            return false;
        }
    }
}
