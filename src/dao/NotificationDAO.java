package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Notification;
import utils.DatabaseConnection;

public class NotificationDAO {

    public boolean createNotification(int userId, String text) {
        String sql = "INSERT INTO notifications (userId, text, isRead) VALUES (?, ?, false)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, text);
            return preparedStatement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Notification> getUnreadNotifications(int userId) {
        ArrayList<Notification> notifications = new ArrayList<>();
        String sql = " SELECT * FROM notifications WHERE userId = ? AND isRead = false ORDER BY createdAt DESC ";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                notifications.add(mapResultSetToNotification(resultSet));
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET isRead = true WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, notificationId);
            return preparedStatement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            return false;
        }
    }

    private Notification mapResultSetToNotification(ResultSet resultSet) throws SQLException {
        Notification notification = new Notification();
        notification.setId(resultSet.getInt("id"));
        notification.setUserId(resultSet.getInt("userId"));
        notification.setText(resultSet.getString("text"));
        notification.setRead(resultSet.getBoolean("isRead"));
        notification.setCreatedAt(resultSet.getTimestamp("createdAt").toLocalDateTime());
        return notification;
    }
}
