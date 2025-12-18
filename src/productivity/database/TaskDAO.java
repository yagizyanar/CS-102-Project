package productivity.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import productivity.task.Goal;
import src.productivity.task.Task;

public class TaskDAO {
    private static List<Task> tasks;
    private static List<Goal> goals;
    
    public static void updateTaskInDatabase(Task task) {
    
    String UPDATE_SQL = "UPDATE tasks SET title=?, description=?, deadline=?, completed=?, xp_reward=? WHERE id=?";

    try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

                    ps.setString(1, task.getTitle());
                    ps.setString(2, task.getDescription());
                    ps.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
                    ps.setBoolean(4, task.isComplete());
                    ps.setInt(5, task.getXpReward());
                    ps.setInt(6, task.getId()); 

                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("database error updating task: " + e.getMessage());
                }
            }   

    /**
     * Loads all tasks and goals from the remote MySQL database and loads them into memory.
     */
    public static void loadFromDatabase() {
        String SELECT_TASKS = "SELECT id, title, description, deadline, completed, xp_reward FROM tasks";
        String SELECT_GOALS = "SELECT id, title, event_type, event_date, notes FROM events";
            
        tasks.clear(); 
        goals.clear(); 

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_TASKS)) {

                while (rs.next()) {
                    LocalDateTime deadline = rs.getTimestamp("deadline").toLocalDateTime(); 
                    Task task = new Task(rs.getInt("id"), rs.getString("title"), rs.getString("description"),deadline, rs.getBoolean("completed"), rs.getInt("xp_reward"));
                    tasks.add(task);
                }
            } catch (SQLException e) {
                System.err.println("database error loading tasks: " + e.getMessage());
            }
        
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_GOALS)) {

                while (rs.next()) {
                    LocalDateTime targetDate = rs.getTimestamp("event_date").toLocalDateTime(); 
                    Goal goal = new Goal(rs.getInt("id"), rs.getString("title"), rs.getString("notes"), targetDate, false, rs.getString("event_type"));
                    goals.add(goal);
                }
            } catch (SQLException e) {
                System.err.println("database error loading goals: " + e.getMessage());
            }
    }

    /**
     * Writes new Task object to the remote MySQL 'tasks' table
     * @param task task to be saved
     */
    public static void saveTaskToDatabase(Task task) {
        String INSERT_SQL = "INSERT INTO tasks (title, description, deadline, completed, xp_reward) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            ps.setBoolean(4, task.isComplete());
            ps.setInt(5, task.getXpReward());

            if (ps.executeUpdate() > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        task.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("database error: " + e.getMessage());
        }
    }

    /**
     * Writes a new Goal object to the remote MySQL 'events' table.
     * We save Goal as a special type of event.
     * @param goal goal to be saved
     */
    public static void saveGoalToDatabase(Goal goal) {
        String INSERT_SQL = "INSERT INTO events (title, event_type, event_date, notes) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, goal.getTitle());
            ps.setString(2, goal.getCategory()); 
            ps.setTimestamp(3, Timestamp.valueOf(goal.getTargetDate())); 
            ps.setString(4, goal.getDescription()); 

            if (ps.executeUpdate() > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goal.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("database error saving goal: " + e.getMessage());
        }
    }

}
