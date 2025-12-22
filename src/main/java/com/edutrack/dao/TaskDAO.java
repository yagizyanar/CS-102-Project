package com.edutrack.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.edutrack.model.Task;
import com.edutrack.util.DatabaseManager;

public class TaskDAO {

    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks(user_id, title, description, due_date, status, course_tag) VALUES(?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, task.getUserId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getDueDate());
            pstmt.setString(5, task.getStatus());
            pstmt.setString(6, task.getCourseTag());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("due_date"),
                        rs.getString("status"),
                        rs.getString("course_tag")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tasks;
    }

    public void updateTaskStatus(int taskId, String status) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
