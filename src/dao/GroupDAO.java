package dao;

import java.sql.*;
import java.util.ArrayList;
import models.user.User;

public class GroupDAO {

    public int createGroup(String groupName, int creatorId) {
        String sql = "INSERT INTO studyGroups (name, creatorId) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, groupName);
            preparedStatement.setInt(2, creatorId);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                int groupId = resultSet.getInt(1);
                addMember(groupId, creatorId);
                return groupId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addMember(int groupId, int userId) {
        String sql = "INSERT INTO groupMembers (groupId, userId, isReady) VALUES (?, ?, false)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            return false;
        }
    }

    public boolean updateMemberStatus(int groupId, int userId, boolean isReady) {
        String sql = "UPDATE groupMembers SET isReady = ? WHERE groupId = ? AND userId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBoolean(1, isReady);
            preparedStatement.setInt(2, groupId);
            preparedStatement.setInt(3, userId);

            return preparedStatement.executeUpdate() > 0;

        } 
        catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<User> getGroupMembers(int groupId) {
        ArrayList<User> members = new ArrayList<>();
        String sql = "SELECT users.id, users.username, users.email, users.password " +
                     "FROM users INNER JOIN groupMembers ON users.id = groupMembers.userId " +
                     "WHERE groupMembers.groupId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, groupId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                members.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }


    public boolean isAllReady(int groupId) {
        String sql =
            "SELECT " +
            "COUNT(*) AS totalMembers, " +
            "SUM(CASE WHEN isReady = TRUE THEN 1 ELSE 0 END) AS readyMembers " +
            "FROM groupMembers " +
            "WHERE groupId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int totalMembers = resultSet.getInt("totalMembers");
                int readyMembers = resultSet.getInt("readyMembers");

                return totalMembers > 0 && totalMembers == readyMembers;
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
