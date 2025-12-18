package dao;

import java.sql.*;
import java.util.ArrayList;
import models.courses.Course;

public class CourseDAO {
    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                courses.add(mapRow(resultSet));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public ArrayList<Course> getCoursesByUserId(int userId) {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                     "JOIN studentCourses sc ON c.id = sc.courseId " +
                     "WHERE sc.studentId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                courses.add(mapRow(resultSet));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public boolean enrollUser(int userId, int courseId) {
        String sql = "INSERT INTO studentCourses (studentId, courseId) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, courseId);
            return preparedStatement.executeUpdate() > 0;
        } 
        catch (SQLException e) {
            return false;
        }
    }

    private Course mapRow(ResultSet resultSet) throws SQLException {
        Course course = new Course();
        course.setCourseId(resultSet.getInt("id"));
        course.setCourseCode(resultSet.getString("courseCode"));
        course.setCourseName(resultSet.getString("courseName"));
        return course;
    }
}
