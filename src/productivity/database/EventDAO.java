package productivity.database;

import productivity.event.Event;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventDAO {
    private static ArrayList<Event> events;
    
    public static void saveEventToDatabase(Event event) {
        String sql = "INSERT INTO events(title, event_type, event_date, notes) VALUES(?,?,?,?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getType());
            pstmt.setTimestamp(3, Timestamp.valueOf(event.getDate()));
            pstmt.setString(4, event.getNotes());

            if (pstmt.executeUpdate() > 0) {
                 try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                       event.setId(rs.getInt(1));
                    }
                }
            } 
            
        } catch (SQLException e) {
            System.err.println("Couldn't save event: " + e.getMessage());
        }
    }

    public static void deleteEventFromDatabase(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
        }
    }

    public static void loadFromDatabase() {
        String sql = "SELECT * FROM events WHERE event_date >= datetime('now', '-1 day')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                LocalDateTime eventDate = rs.getTimestamp("event_date").toLocalDateTime();
                
                Event event = new Event(rs.getInt("id"), rs.getString("title"), rs.getString("event_type"), eventDate, rs.getString("notes"));
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
    }
}
