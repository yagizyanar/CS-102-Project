package com.edutrack.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseManager {
    private static Properties props = new Properties();
    private static String dbType = "mysql"; // Default to MySQL
    private static boolean initialized = false; // Track if already initialized

    // Hardcoded MySQL settings for remote database
    private static final String MYSQL_URL = "jdbc:mysql://bovmfyrxqnhunkngnlkb-mysql.services.clever-cloud.com:20497/bovmfyrxqnhunkngnlkb";
    private static final String MYSQL_USER = "uia9z2gxkded5h1h";
    private static final String MYSQL_PASSWORD = "qfhfxDdMBUPViOcFrSE";

    // Connection pool settings
    private static final int POOL_SIZE = 5;
    private static BlockingQueue<Connection> connectionPool;
    private static boolean poolInitialized = false;

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                dbType = props.getProperty("db.type", "mysql");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initialize the connection pool with pre-established connections.
     * This should be called once at application startup.
     */
    private static synchronized void initializePool() {
        if (poolInitialized) {
            return;
        }

        connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
        String url = props.getProperty("mysql.url", MYSQL_URL);
        String user = props.getProperty("mysql.user", MYSQL_USER);
        String password = props.getProperty("mysql.password", MYSQL_PASSWORD);

        System.out.println("Initializing connection pool with " + POOL_SIZE + " connections...");

        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                connectionPool.offer(conn);
                System.out.println("Pool connection " + (i + 1) + " established.");
            } catch (SQLException e) {
                System.out.println("Failed to create pool connection " + (i + 1) + ": " + e.getMessage());
            }
        }

        poolInitialized = true;
        System.out.println("Connection pool initialized with " + connectionPool.size() + " connections.");
    }

    public static Connection connect() {
        // Initialize pool if not already done
        if (!poolInitialized) {
            initializePool();
        }

        Connection conn = null;
        try {
            // Try to get a connection from the pool
            conn = connectionPool.poll();

            if (conn != null) {
                // Validate the connection is still alive
                if (conn.isClosed() || !conn.isValid(1)) {
                    // Connection is dead, create a new one
                    String url = props.getProperty("mysql.url", MYSQL_URL);
                    String user = props.getProperty("mysql.user", MYSQL_USER);
                    String password = props.getProperty("mysql.password", MYSQL_PASSWORD);
                    conn = DriverManager.getConnection(url, user, password);
                }
            } else {
                // Pool is empty, create a new connection
                String url = props.getProperty("mysql.url", MYSQL_URL);
                String user = props.getProperty("mysql.user", MYSQL_USER);
                String password = props.getProperty("mysql.password", MYSQL_PASSWORD);
                conn = DriverManager.getConnection(url, user, password);
            }

            // Wrap the connection to return it to the pool when closed
            return new PooledConnection(conn, connectionPool);

        } catch (SQLException e) {
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Wrapper class that returns the connection to the pool instead of closing it.
     */
    private static class PooledConnection implements Connection {
        private Connection realConnection;
        private BlockingQueue<Connection> pool;
        private boolean isClosed = false;

        public PooledConnection(Connection conn, BlockingQueue<Connection> pool) {
            this.realConnection = conn;
            this.pool = pool;
        }

        @Override
        public void close() throws SQLException {
            if (!isClosed && realConnection != null && !realConnection.isClosed()) {
                // Return to pool instead of closing
                if (!pool.offer(realConnection)) {
                    // Pool is full, actually close the connection
                    realConnection.close();
                }
            }
            isClosed = true;
        }

        // Delegate all other methods to the real connection
        @Override
        public Statement createStatement() throws SQLException {
            return realConnection.createStatement();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            return realConnection.prepareStatement(sql);
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return realConnection.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return realConnection.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            realConnection.setAutoCommit(autoCommit);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return realConnection.getAutoCommit();
        }

        @Override
        public void commit() throws SQLException {
            realConnection.commit();
        }

        @Override
        public void rollback() throws SQLException {
            realConnection.rollback();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return isClosed || realConnection.isClosed();
        }

        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return realConnection.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            realConnection.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return realConnection.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            realConnection.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            return realConnection.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            realConnection.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return realConnection.getTransactionIsolation();
        }

        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return realConnection.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            realConnection.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return realConnection.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            return realConnection.getTypeMap();
        }

        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            realConnection.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            realConnection.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            return realConnection.getHoldability();
        }

        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return realConnection.setSavepoint();
        }

        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return realConnection.setSavepoint(name);
        }

        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            realConnection.rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            realConnection.releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return realConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return realConnection.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return realConnection.prepareStatement(sql, columnIndexes);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return realConnection.prepareStatement(sql, columnNames);
        }

        @Override
        public java.sql.Clob createClob() throws SQLException {
            return realConnection.createClob();
        }

        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return realConnection.createBlob();
        }

        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return realConnection.createNClob();
        }

        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return realConnection.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return realConnection.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            realConnection.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            realConnection.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return realConnection.getClientInfo(name);
        }

        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return realConnection.getClientInfo();
        }

        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return realConnection.createArrayOf(typeName, elements);
        }

        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return realConnection.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            realConnection.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            return realConnection.getSchema();
        }

        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            realConnection.abort(executor);
        }

        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            realConnection.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return realConnection.getNetworkTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return realConnection.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return realConnection.isWrapperFor(iface);
        }
    }

    public static void initialize() {
        // Skip if already initialized this session
        if (initialized) {
            return;
        }
        initialized = true;

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
                } catch (Exception e) {
                }
                try {
                    stmt.execute(groupMembersTable);
                } catch (Exception e) {
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

                // STUDY_GROUPS Table - Timer sync columns
                try {
                    stmt.execute("ALTER TABLE study_groups ADD COLUMN timer_start_time BIGINT DEFAULT 0");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE study_groups ADD COLUMN timer_duration INTEGER DEFAULT 0");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE study_groups ADD COLUMN timer_running BOOLEAN DEFAULT FALSE");
                } catch (Exception e) {
                }
                try {
                    stmt.execute("ALTER TABLE study_groups ADD COLUMN timer_study_phase BOOLEAN DEFAULT TRUE");
                } catch (Exception e) {
                }
                // timestamp/created_at might be tricky on MySQL 5.x vs 8.x, skipping for now
                // unless reported.
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
