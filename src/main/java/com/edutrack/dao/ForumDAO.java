package com.edutrack.dao;

import com.edutrack.model.ForumPost;
import com.edutrack.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumDAO {

    public void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS forum_posts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "course_code TEXT NOT NULL,"
                + "username TEXT NOT NULL,"
                + "content TEXT NOT NULL,"
                + "timestamp TEXT DEFAULT CURRENT_TIMESTAMP"
                + ");";
        try (Connection conn = DatabaseManager.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addPost(ForumPost post) {
        String sql = "INSERT INTO forum_posts(course_code, username, content) VALUES(?,?,?)";
        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, post.getCourseCode());
            pstmt.setString(2, post.getUsername());
            pstmt.setString(3, post.getContent());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<ForumPost> getPostsByCourse(String courseCode) {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT * FROM forum_posts WHERE course_code = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(new ForumPost(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("username"),
                        rs.getString("content"),
                        rs.getString("timestamp")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return posts;
    }
}
