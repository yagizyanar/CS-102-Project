package src.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:edutrack.db";
    private static Connection connection = null;

    // Get database connection (Singleton pattern)
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Database connection established");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize database tables
    public static void initializeDatabase() {
        Connection conn = getConnection();

        try (Statement stmt = conn.createStatement()) {

            // Create Users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    profile_picture TEXT DEFAULT 'default.png',
                    xp_amount INTEGER DEFAULT 0,
                    current_streak INTEGER DEFAULT 0,
                    last_login_date TEXT,
                    registration_date TEXT NOT NULL
                )
            """;
            stmt.execute(createUsersTable);

            // Create Friendships table
            String createFriendshipsTable = """
                CREATE TABLE IF NOT EXISTS friendships (
                    friendship_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender_id INTEGER NOT NULL,
                    receiver_id INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    request_date TEXT NOT NULL,
                    response_date TEXT,
                    FOREIGN KEY (sender_id) REFERENCES users(user_id),
                    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
                )
            """;
            stmt.execute(createFriendshipsTable);

            // Create Notifications table
            String createNotificationsTable = """
                CREATE TABLE IF NOT EXISTS notifications (
                    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    title TEXT NOT NULL,
                    message TEXT NOT NULL,
                    is_read INTEGER DEFAULT 0,
                    created_at TEXT NOT NULL,
                    read_at TEXT,
                    related_entity_id INTEGER,
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                )
            """;
            stmt.execute(createNotificationsTable);

            // Create Badges table
            String createBadgesTable = """
                CREATE TABLE IF NOT EXISTS badges (
                    badge_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    icon_path TEXT,
                    category TEXT NOT NULL,
                    required_value INTEGER NOT NULL
                )
            """;
            stmt.execute(createBadgesTable);

            // Create User_Badges junction table
            String createUserBadgesTable = """
                CREATE TABLE IF NOT EXISTS user_badges (
                    user_id INTEGER NOT NULL,
                    badge_id INTEGER NOT NULL,
                    earned_date TEXT NOT NULL,
                    PRIMARY KEY (user_id, badge_id),
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    FOREIGN KEY (badge_id) REFERENCES badges(badge_id)
                )
            """;
            stmt.execute(createUserBadgesTable);

            // Create Enrolled_Courses table
            String createEnrolledCoursesTable = """
                CREATE TABLE IF NOT EXISTS enrolled_courses (
                    user_id INTEGER NOT NULL,
                    course_id INTEGER NOT NULL,
                    enrollment_date TEXT NOT NULL,
                    PRIMARY KEY (user_id, course_id),
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                )
            """;
            stmt.execute(createEnrolledCoursesTable);

            System.out.println("Database tables initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}