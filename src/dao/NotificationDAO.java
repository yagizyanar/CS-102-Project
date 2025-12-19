package dao;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;

import models.user.Notification; 
import dao.DatabaseConnection; // Assuming DatabaseConnection moved to dao package

public class NotificationDAO {

    /**
     * Creates a new notification in the database.
     */
    public boolean createNotification(int userId, Notification.NotificationType type, String title, String message) {
        String sql = "INSERT INTO notifications (userId, type, title, message, isRead, createdAt) VALUES (?, ?, ?, ?, false, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, type.name()); 
            preparedStatement.setString(3, title);
            preparedStatement.setString(4, message);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves unread notifications for a specific user.
     */
    public ArrayList<Notification> getUnreadNotifications(int userId) {
        ArrayList<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE userId = ? AND isRead = false ORDER BY createdAt DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                notifications.add(mapResultSetToNotification(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Updates the status of a notification to 'read'.
     */
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET isRead = true, readAt = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(2, notificationId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        return new Notification(
            rs.getInt("id"),
            rs.getInt("userId"),
            Notification.NotificationType.valueOf(rs.getString("type")),
            rs.getString("title"),
            rs.getString("message"),
            rs.getBoolean("isRead"),
            rs.getTimestamp("createdAt").toLocalDateTime(),
            rs.getTimestamp("readAt") != null ? rs.getTimestamp("readAt").toLocalDateTime() : null,
            (Integer) rs.getObject("relatedEntityId")
        );
    }
}