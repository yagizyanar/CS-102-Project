package com.edutrack.dao;

import com.edutrack.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "INSERT INTO events(user_id, type, name, event_date, note) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setString(3, name);
            pstmt.setString(4, eventDate);
            pstmt.setString(5, note);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding event: " + e.getMessage());
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
