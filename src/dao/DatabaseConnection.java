package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;
    
    private static final String HOST = "bovmfyrxqnhunkngnlkb-mysql.services.clever-cloud.com"; 
    private static final String PORT = "20497";
    private static final String DB_NAME = "bovmfyrxqnhunkngnlkb";
    private static final String USER = "uia9z2gxkded5h1h";
    private static final String PASSWORD = "qfhfxDdMBUPViOcFrSE";

    private static final String MYSQL_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(MYSQL_URL, USER, PASSWORD);
                System.out.println("database connected");
            } catch (Exception e) {
                System.err.println("database couldn't be connected");
                e.printStackTrace();
            }
        }
        return connection;
    }
        
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
