package com.edutrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectivityTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://20497/bovmfyrxqnhunkngnlkb";
        String user = "uia9z2gxkded5h1h";
        
        String pass = "qfhfxDdMBUPViOcFrSE";

        System.out.println("Testing connectivity to: " + url);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("SUCCESS: Remote Database is reachable!");
        } catch (SQLException e) {
            System.out.println("FAILURE: " + e.getMessage());
            System.out.println("Make sure usage is: jdbc:mysql://HOST:PORT/DB_NAME");
        }
    }
}
