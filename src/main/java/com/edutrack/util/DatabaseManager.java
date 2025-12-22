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
    private static String dbType = "sqlite";

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                dbType = props.getProperty("db.type", "sqlite");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            if ("mysql".equalsIgnoreCase(dbType)) {
                conn = DriverManager.getConnection(
                        props.getProperty("mysql.url"),
                        props.getProperty("mysql.user"),
                        props.getProperty("mysql.password"));
            } else {
                conn = DriverManager.getConnection(props.getProperty("sqlite.url", "jdbc:sqlite:edutrack.db"));
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
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
        try (Connection conn = connect()) {
            if (conn == null) {
                System.out.println("CRITICAL ERROR: Could not connect to database. Check db.properties.");
                return;
            }
            try (Statement stmt = conn.createStatement()) {
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

                // FRIENDSHIPS Table
                try {
                    stmt.execute("ALTER TABLE friendships ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING'");
                } catch (Exception e) {
                }

                System.out.println("Schema migration attempts completed.");

                System.out.println("Database initialized (" + dbType + ").");
            }
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
