/**
 * Data Access Object for managing Badge entities and user-badge relationships in the database.
 * Provides static methods for CRUD operations on badges, awarding/removing badges to/from users,
 * and querying badge collections. Manages two database tables: 'badges' for badge definitions
 * and 'user_badges' as a junction table tracking which users have earned which badges.
 *
 * @author Javanshir Aghayev
 */

package src.dao;

import com.edutrack.model.Badge;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BadgeDAO {
    public static void createBadgesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS badges (
                badge_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                icon_path TEXT,
                category TEXT NOT NULL,
                required_value INTEGER NOT NULL
            )
        """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Badges table created successfully");

        } catch (SQLException e) {
            System.err.println("Error creating badges table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createUserBadgesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS user_badges (
                user_id INTEGER NOT NULL,
                badge_id INTEGER NOT NULL,
                earned_date TEXT NOT NULL,
                PRIMARY KEY (user_id, badge_id),
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (badge_id) REFERENCES badges(badge_id)
            )
        """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("User_badges table created successfully");

        } catch (SQLException e) {
            System.err.println("Error creating user_badges table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Badge insertBadge(Badge badge) {
        String sql = "INSERT INTO badges (name, description, icon_path, category, required_value) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, badge.getName());
            pstmt.setString(2, badge.getDescription());
            pstmt.setString(3, badge.getIconPath());
            pstmt.setString(4, badge.getCategory().name());
            pstmt.setInt(5, badge.getRequiredValue());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int badgeId = generatedKeys.getInt(1);
                    // Create a new badge object with the generated ID
                    Badge newBadge = new Badge(badgeId, badge.getName(), badge.getDescription(),
                            badge.getIconPath(), badge.getCategory(), badge.getRequiredValue());
                    System.out.println("Badge inserted successfully with ID: " + badgeId);
                    return newBadge;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting badge: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static Badge getBadgeById(int badgeId) {
        String sql = "SELECT * FROM badges WHERE badge_id = ?";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, badgeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBadgeFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting badge by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static List<Badge> getAllBadges() {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT * FROM badges ORDER BY category, required_value";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                badges.add(extractBadgeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all badges: " + e.getMessage());
            e.printStackTrace();
        }

        return badges;
    }

    public static List<Badge> getBadgesByCategory(Badge.BadgeCategory category) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT * FROM badges WHERE category = ? ORDER BY required_value";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                badges.add(extractBadgeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting badges by category: " + e.getMessage());
            e.printStackTrace();
        }

        return badges;
    }

    public static boolean awardBadgeToUser(int userId, int badgeId) {
        if (userHasBadge(userId, badgeId)) {
            System.out.println("User already has this badge");
            return false;
        }

        String sql = "INSERT INTO user_badges (user_id, badge_id, earned_date) VALUES (?, ?, ?)";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, badgeId);
            pstmt.setString(3, LocalDate.now().toString());

            pstmt.executeUpdate();
            System.out.println("Badge " + badgeId + " awarded to user " + userId);
            return true;

        } catch (SQLException e) {
            System.err.println("Error awarding badge to user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static boolean userHasBadge(int userId, int badgeId) {
        String sql = "SELECT COUNT(*) FROM user_badges WHERE user_id = ? AND badge_id = ?";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, badgeId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking if user has badge: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static List<Badge> getUserBadges(int userId) {
        List<Badge> badges = new ArrayList<>();
        String sql = """
            SELECT b.* FROM badges b
            INNER JOIN user_badges ub ON b.badge_id = ub.badge_id
            WHERE ub.user_id = ?
            ORDER BY ub.earned_date DESC
        """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                badges.add(extractBadgeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting user badges: " + e.getMessage());
            e.printStackTrace();
        }

        return badges;
    }

    public static int getUserBadgeCount(int userId) {
        String sql = "SELECT COUNT(*) FROM user_badges WHERE user_id = ?";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user badge count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean updateBadge(Badge badge) {
        String sql = "UPDATE badges SET name = ?, description = ?, icon_path = ?, category = ?, required_value = ? WHERE badge_id = ?";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, badge.getName());
            pstmt.setString(2, badge.getDescription());
            pstmt.setString(3, badge.getIconPath());
            pstmt.setString(4, badge.getCategory().name());
            pstmt.setInt(5, badge.getRequiredValue());
            pstmt.setInt(6, badge.getBadgeId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Badge updated successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating badge: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteBadge(int badgeId) {
        String deleteUserBadges = "DELETE FROM user_badges WHERE badge_id = ?";
        String deleteBadge = "DELETE FROM badges WHERE badge_id = ?";

        try (Connection conn = dao.DatabaseConnection.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserBadges)) {
                pstmt.setInt(1, badgeId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteBadge)) {
                pstmt.setInt(1, badgeId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Badge deleted successfully");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error deleting badge: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static boolean removeBadgeFromUser(int userId, int badgeId) {
        String sql = "DELETE FROM user_badges WHERE user_id = ? AND badge_id = ?";

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, badgeId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Badge removed from user successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error removing badge from user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static void initializeDefaultBadges() {
        if (getAllBadges().size() > 0) {
            System.out.println("Badges already initialized");
            return;
        }

        System.out.println("Initializing default badges...");

        Badge streak1 = new Badge(0, "Day One", "Login for 1 day", "badges/streak_1.png",
                Badge.BadgeCategory.STREAK, 1);
        Badge streak7 = new Badge(0, "Week Warrior", "Maintain a 7-day streak", "badges/streak_7.png",
                Badge.BadgeCategory.STREAK, 7);
        Badge streak30 = new Badge(0, "Monthly Master", "Maintain a 30-day streak", "badges/streak_30.png",
                Badge.BadgeCategory.STREAK, 30);

        Badge xp100 = new Badge(0, "Getting Started", "Earn 100 XP", "badges/xp_100.png",
                Badge.BadgeCategory.XP, 100);
        Badge xp500 = new Badge(0, "Rising Star", "Earn 500 XP", "badges/xp_500.png",
                Badge.BadgeCategory.XP, 500);
        Badge xp1000 = new Badge(0, "XP Master", "Earn 1000 XP", "badges/xp_1000.png",
                Badge.BadgeCategory.XP, 1000);

        Badge friend1 = new Badge(0, "First Friend", "Add your first friend", "badges/social_1.png",
                Badge.BadgeCategory.SOCIAL, 1);
        Badge friend5 = new Badge(0, "Social Butterfly", "Have 5 friends", "badges/social_5.png",
                Badge.BadgeCategory.SOCIAL, 5);
        Badge friend10 = new Badge(0, "Popular", "Have 10 friends", "badges/social_10.png",
                Badge.BadgeCategory.SOCIAL, 10);

        Badge course1 = new Badge(0, "Student", "Enroll in your first course", "badges/course_1.png",
                Badge.BadgeCategory.COURSE, 1);
        Badge course3 = new Badge(0, "Dedicated Learner", "Enroll in 3 courses", "badges/course_3.png",
                Badge.BadgeCategory.COURSE, 3);
        Badge course5 = new Badge(0, "Knowledge Seeker", "Enroll in 5 courses", "badges/course_5.png",
                Badge.BadgeCategory.COURSE, 5);

        insertBadge(streak1);
        insertBadge(streak7);
        insertBadge(streak30);
        insertBadge(xp100);
        insertBadge(xp500);
        insertBadge(xp1000);
        insertBadge(friend1);
        insertBadge(friend5);
        insertBadge(friend10);
        insertBadge(course1);
        insertBadge(course3);
        insertBadge(course5);

        System.out.println("Default badges initialized successfully");
    }

    private static Badge extractBadgeFromResultSet(ResultSet rs) throws SQLException {
        int badgeId = rs.getInt("badge_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String iconPath = rs.getString("icon_path");
        String categoryStr = rs.getString("category");
        int requiredValue = rs.getInt("required_value");

        Badge.BadgeCategory category = Badge.BadgeCategory.valueOf(categoryStr);

        return new Badge(badgeId, name, description, iconPath, category, requiredValue);
    }
}
