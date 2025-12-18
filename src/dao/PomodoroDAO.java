package dao;

import java.sql.*;
import java.util.ArrayList;
import model.PomodoroSession;
import utils.DatabaseConnection;

public class PomodoroDAO {

    public boolean saveSession(PomodoroSession session) {

        String sql =
            "INSERT INTO pomodoro_sessions " +
            "(userId, groupId, sessionType, startTime, endTime, durationMinutes) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, session.getUserId());

            if (session.getGroupId() != null) {
                preparedStatement.setInt(2, session.getGroupId());
            } 
            else {
                preparedStatement.setNull(2, Types.INTEGER);
            }

            preparedStatement.setString(3, session.getSessionType().name());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(session.getEndTime()));
            preparedStatement.setInt(6, session.getDurationMinutes());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                session.setId(resultSet.getInt(1));
            }

            return true;

        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<PomodoroSession> getUserSessions(int userId) {
        ArrayList<PomodoroSession> sessions = new ArrayList<>();

        String sql =
            "SELECT * FROM pomodoroSessions " +
            "WHERE userId = ? ORDER BY startTime DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PomodoroSession session = mapRow(resultSet);
                sessions.add(session);
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public ArrayList<PomodoroSession> getGroupSessions(int groupId) {
        ArrayList<PomodoroSession> sessions = new ArrayList<>();

        String sql =
            "SELECT * FROM pomodoroSessions " +
            "WHERE groupId = ? ORDER BY startTime DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PomodoroSession session = mapRow(resultSet);
                sessions.add(session);
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    private PomodoroSession mapRow(ResultSet resultSet) throws SQLException {
        PomodoroSession session = new PomodoroSession();

        session.setId(resultSet.getInt("id"));
        session.setUserId(resultSet.getInt("userId"));

        int groupId = resultSet.getInt("groupId");
        if (!resultSet.wasNull()) {
            session.setGroupId(groupId);
        }

        session.setSessionType(
            PomodoroSession.SessionType.valueOf(
                resultSet.getString("sessionType")
            )
        );

        session.setStartTime(resultSet.getTimestamp("startTime").toLocalDateTime());
        session.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
        session.setDurationMinutes(resultSet.getInt("durationMinutes"));

        return session;
    }
}
