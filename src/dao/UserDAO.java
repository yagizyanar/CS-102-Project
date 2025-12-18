package dao;

import java.sql.*;
import model.User;
import utils.DatabaseConnection;

public class UserDAO {

    public boolean register(User user) {
        String sql = "INSERT INTO users (username, email, password, xp, level, profileImagePath) VALUES (?, ?, ?, 0, 1, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword()); 
            statement.setString(4, "default.png");

            return statement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?  AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfileInfo(int userId, String university, String major, String bio) {
        String sql = "UPDATE users SET university = ?, major = ?, bio = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, university);
            statement.setString(2, major);
            statement.setString(3, bio);
            statement.setInt(4, userId);

            return statement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAvatar(int userId, String imagePath) {
        String sql = "UPDATE users SET profileImagePath = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, imagePath);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addXP(int userId, int xpAmount) {
        String sql = "UPDATE users SET xp = xp + ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, xpAmount);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setBio(resultSet.getString("bio"));
        user.setUniversity(resultSet.getString("university"));
        user.setMajor(resultSet.getString("major"));
        user.setProfileImagePath(resultSet.getString("profileImagePath"));
        user.setXp(resultSet.getInt("xp"));
        user.setLevel(resultSet.getInt("level"));
        return user;
    }
}