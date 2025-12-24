package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import model.ForumPost;
import utils.DatabaseConnection;

public class ForumDAO {
    public boolean createPost(ForumPost post, File fileToSend) {
        String sql = "INSERT INTO forumPosts (courseId, userId, content, hashtag, attachmentPath, attachmentData, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, post.getCourseId());
            preparedStatement.setInt(2, post.getUserId());
            preparedStatement.setString(3, post.getContent());
            preparedStatement.setString(4, post.getHashtag());
            
            if (fileToSend != null && fileToSend.exists()) {
                preparedStatement.setString(5, fileToSend.getName());
                FileInputStream fileStream = new FileInputStream(fileToSend);
                preparedStatement.setBinaryStream(6, fileStream, (int)fileToSend.length());
            } 
            else {
                preparedStatement.setNull(5, Types.VARCHAR);
                preparedStatement.setNull(6, Types.BLOB);
            }

            preparedStatement.setTimestamp(7, Timestamp.valueOf(post.getCreatedAt()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    post.setPostId(resultSet.getInt(1));
                }
                return true;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ForumPost> getPostsByCourseId(int courseId) {
        ArrayList<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT fp.id, fp.courseId, fp.userId, fp.content, fp.hashtag, fp.attachmentPath, fp.createdAt, u.username " +
                     "FROM forumPosts fp " +
                     "JOIN users u ON fp.userId = u.id " +
                     "WHERE fp.courseId = ? " +
                     "ORDER BY fp.createdAt ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ForumPost post = new ForumPost();
                post.setPostId(resultSet.getInt("id"));
                post.setCourseId(resultSet.getInt("courseId"));
                post.setUserId(resultSet.getInt("userId"));
                post.setContent(resultSet.getString("content"));
                post.setHashtag(resultSet.getString("hashtag"));
                post.setAttachmentPath(resultSet.getString("attachmentPath"));
                post.setCreatedAt(resultSet.getTimestamp("createdAt").toLocalDateTime());  
                post.setAuthorName(resultSet.getString("username"));
                posts.add(post);
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public boolean downloadFile(int postId, String destinationPath) {
        String sql = "SELECT attachmentData FROM forumPosts WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                InputStream input = resultSet.getBinaryStream("attachmentData");
                if (input == null) return false;

                FileOutputStream output = new FileOutputStream(destinationPath);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
                input.close();
                return true;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}