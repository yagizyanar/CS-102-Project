package com.edutrack.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private static Properties props = new Properties();
    private static String dbType = "mysql"; // Default to MySQL

    // Hardcoded MySQL settings for remote database
    private static final String MYSQL_URL = "jdbc:mysql://bovmfyrxqnhunkngnlkb-mysql.services.clever-cloud.com:20497/bovmfyrxqnhunkngnlkb";
    private static final String MYSQL_USER = "uia9z2gxkded5h1h";
    private static final String MYSQL_PASSWORD = "qfhfxDdMBUPViOcFrSE";

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                dbType = props.getProperty("db.type", "mysql");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Database type: " + dbType + " (Remote MySQL)");
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            // Always use MySQL remote database
            String url = props.getProperty("mysql.url", MYSQL_URL);
            String user = props.getProperty("mysql.user", MYSQL_USER);
            String password = props.getProperty("mysql.password", MYSQL_PASSWORD);

            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }

    public static void initialize() {
        // SQL Dialect differences
        boolean isMySQL = "mysql".equalsIgnoreCase(dbType);
        String autoIncrement = isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        String currentTimestamp = isMySQL ? "CURRENT_TIMESTAMP" : "datetime('now')";

        // Users Table
        String userTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "username VARCHAR(255) NOT NULL UNIQUE,"
                + "password VARCHAR(255) NOT NULL,"
                + "email VARCHAR(255) NOT NULL UNIQUE,"
                + "major VARCHAR(255)"
                + ");";

        // Tasks Table
        String taskTable = "CREATE TABLE IF NOT EXISTS tasks ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "user_id INTEGER,"
                + "title VARCHAR(255) NOT NULL,"
                + "description TEXT,"
                + "due_date VARCHAR(50),"
                + "status VARCHAR(20) DEFAULT 'PENDING',"
                + "course_tag VARCHAR(50),"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        // Sessions Table
        String sessionTable = "CREATE TABLE IF NOT EXISTS sessions ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "user_id INTEGER,"
                + "start_time VARCHAR(50),"
                + "duration_minutes INTEGER,"
                + "type VARCHAR(20),"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        // Forum Table - Use TIMESTAMP for MySQL compatibility with DEFAULT
        // CURRENT_TIMESTAMP
        String forumTable = "CREATE TABLE IF NOT EXISTS forum_posts ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "course_code VARCHAR(50) NOT NULL,"
                + "username VARCHAR(255) NOT NULL,"
                + "content TEXT NOT NULL,"
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        // Friendships Table
        String friendshipTable = "CREATE TABLE IF NOT EXISTS friendships ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "requester_id INTEGER NOT NULL,"
                + "addressee_id INTEGER NOT NULL,"
                + "status VARCHAR(20) DEFAULT 'PENDING'," // PENDING, ACCEPTED, REJECTED
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(requester_id) REFERENCES users(id),"
                + "FOREIGN KEY(addressee_id) REFERENCES users(id),"
                + "UNIQUE(requester_id, addressee_id)"
                + ");";

        // Goals Table
        String goalsTable = "CREATE TABLE IF NOT EXISTS goals ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "user_id INTEGER NOT NULL,"
                + "name VARCHAR(255) NOT NULL,"
                + "deadline VARCHAR(50),"
                + "completed BOOLEAN DEFAULT FALSE,"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        // Events Table
        String eventsTable = "CREATE TABLE IF NOT EXISTS events ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "user_id INTEGER NOT NULL,"
                + "type VARCHAR(50) NOT NULL,"
                + "name VARCHAR(255) NOT NULL,"
                + "event_date VARCHAR(50),"
                + "note TEXT,"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        // Groups Table (using study_groups to avoid SQL reserved keyword)
        String groupsTable = "CREATE TABLE IF NOT EXISTS study_groups ("
                + "id INTEGER PRIMARY KEY " + autoIncrement + ","
                + "name VARCHAR(255) NOT NULL UNIQUE,"
                + "owner_id INTEGER NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(owner_id) REFERENCES users(id)"
                + ");";

        // Group Members Table
        String groupMembersTable = "CREATE TABLE IF NOT EXISTS group_members ("
                + "group_id INTEGER NOT NULL,"
                + "user_id INTEGER NOT NULL,"
                + "is_ready BOOLEAN DEFAULT FALSE,"
                + "joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY(group_id, user_id),"
                + "FOREIGN KEY(group_id) REFERENCES study_groups(id),"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        try (Connection conn = connect()) {
            if (conn == null) {
                System.out.println("CRITICAL ERROR: Could not connect to database. Check db.properties.");
                return;
            }
            try (Statement stmt = conn.createStatement()) {
                // Execute each table creation separately
                try {
                    stmt.execute(userTable);
                } catch (Exception e) {
                    System.out.println("Error creating users: " + e.getMessage());
                }
                try {
                    stmt.execute(taskTable);
                } catch (Exception e) {
                    System.out.println("Error creating tasks: " + e.getMessage());
                }
                try {
                    stmt.execute(sessionTable);
                } catch (Exception e) {
                    System.out.println("Error creating sessions: " + e.getMessage());
                }
                try {
                    stmt.execute(forumTable);
                } catch (Exception e) {
                    System.out.println("Error creating forum_posts: " + e.getMessage());
                }
                try {
                    stmt.execute(friendshipTable);
                } catch (Exception e) {
                    System.out.println("Error creating friendships: " + e.getMessage());
                }
                try {
                    stmt.execute(goalsTable);
                } catch (Exception e) {
                    System.out.println("Error creating goals: " + e.getMessage());
                }
                try {
                    stmt.execute(eventsTable);
                } catch (Exception e) {
                    System.out.println("Error creating events: " + e.getMessage());
                }
                try {
                    stmt.execute(groupsTable);
                    System.out.println("SUCCESS: study_groups table created/verified");
                } catch (Exception e) {
                    System.out.println("Error creating study_groups: " + e.getMessage());
                }
                try {
                    stmt.execute(groupMembersTable);
                    System.out.println("SUCCESS: group_members table created/verified");
                } catch (Exception e) {
                    System.out.println("Error creating group_members: " + e.getMessage());
                }

                // --- COMPREHENSIVE SCHEMA MIGRATION ---
                // We attempt to add ALL non-primary key columns.
                // If they exist, it throws an exception which we safely ignore.

                // USERS Table
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN major VARCHAR(255)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN university VARCHAR(255)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN bio TEXT");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN notes TEXT");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN profile_picture VARCHAR(255)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN points INTEGER DEFAULT 0");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN classes TEXT");
                } catch (Exception e) {
                }

                // TASKS Table
                try {
                    stmt.execute("ALTER TABLE tasks ADD COLUMN user_id INTEGER");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE tasks ADD COLUMN description TEXT");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE tasks ADD COLUMN due_date VARCHAR(50)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE tasks ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING'");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE tasks ADD COLUMN course_tag VARCHAR(50)");
                } catch (Exception e) {
                }

                // SESSIONS Table
                try {
                    stmt.execute("ALTER TABLE sessions ADD COLUMN type VARCHAR(20)");
                } catch (Exception e) {
                }

                try {
                    stmt.execute("ALTER TABLE friendships ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING'");
                } catch (Exception e) {
                }

                // GOALS Table
                try {
                    stmt.execute("ALTER TABLE goals ADD COLUMN user_id INTEGER");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE goals ADD COLUMN name VARCHAR(255)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE goals ADD COLUMN deadline VARCHAR(50)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE goals ADD COLUMN completed BOOLEAN DEFAULT FALSE");
                } catch (Exception e) {
                }

                // EVENTS Table
                try {
                    stmt.execute("ALTER TABLE events ADD COLUMN user_id INTEGER");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE events ADD COLUMN type VARCHAR(50)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE events ADD COLUMN name VARCHAR(255)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE events ADD COLUMN event_date VARCHAR(50)");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE events ADD COLUMN note TEXT");
                } catch (Exception e) {
                }
                // timestamp/created_at might be tricky on MySQL 5.x vs 8.x, skipping for now
                // unless reported.

                System.out.println("Schema migration attempts completed.");

                System.out.println("Database initialized (" + dbType + ").");
            }

            // Initialize badge system
            BadgeService.initialize();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Tester Method
    public static void main(String[] args) {
        System.out.println("--- Testing Database Connection ---");
        System.out.println("Read DB Type: " + dbType);
        Connection conn = connect();
        if (conn != null) {
            System.out.println("SUCCESS: Connection established!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("FAILURE: Could not connect.");
        }
    }
}
