package com.edutrack.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.edutrack.util.DatabaseManager;

public class EventDAO {

    public static class EventRecord {
        public int id;
        public int userId;
        public String type;
        public String name;
        public String eventDate;
        public String note;

        public EventRecord(int id, int userId, String type, String name, String eventDate, String note) {
            this.id = id;
            this.userId = userId;
            this.type = type;
            this.name = name;
            this.eventDate = eventDate;
            this.note = note;
        }
    }

    public boolean addEvent(int userId, String type, String name, String eventDate, String note) {
        System.out.println(
                "DEBUG addEvent: userId=" + userId + ", type=" + type + ", name=" + name + ", date=" + eventDate);
        // Include 'title' column which exists in the MySQL database
        String sql = "INSERT INTO events(user_id, type, name, title, event_date, note) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.out.println("DEBUG addEvent: Connection is NULL!");
                return false;
            }
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setString(3, name);
            pstmt.setString(4, name); // Use name as title as well
            pstmt.setString(5, eventDate);
            pstmt.setString(6, note);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("DEBUG addEvent: Rows affected = " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding event: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    public List<EventRecord> getEventsByUserId(int userId) {
        List<EventRecord> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(new EventRecord(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("name"),
                        rs.getString("event_date"),
                        rs.getString("note")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting events: " + e.getMessage());
        }
        return events;
    }

    public void deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting event: " + e.getMessage());
        }
    }
}
