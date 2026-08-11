package com.novastudent.dao;

import com.novastudent.database.DatabaseConnection;
import com.novastudent.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entity.
 */
public class CourseDAO {

    private final DatabaseConnection dbConnection;

    public CourseDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT c.*, d.department_name, d.department_code " +
                     "FROM courses c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "WHERE c.is_active = TRUE ORDER BY c.course_code";
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(mapResultSet(rs));
            }
        }
        return courses;
    }

    public List<Course> getByDepartment(int departmentId) throws SQLException {
        String sql = "SELECT c.*, d.department_name, d.department_code " +
                     "FROM courses c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "WHERE c.department_id = ? AND c.is_active = TRUE ORDER BY c.semester, c.course_code";
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSet(rs));
                }
            }
        }
        return courses;
    }

    public List<Course> getBySemester(int semester) throws SQLException {
        String sql = "SELECT c.*, d.department_name, d.department_code " +
                     "FROM courses c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "WHERE c.semester = ? AND c.is_active = TRUE ORDER BY c.course_code";
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, semester);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSet(rs));
                }
            }
        }
        return courses;
    }

    public Course findById(int id) throws SQLException {
        String sql = "SELECT c.*, d.department_name, d.department_code " +
                     "FROM courses c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "WHERE c.id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        }
        return null;
    }

    public int insert(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, credits, department_id, semester, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getDepartmentId());
            stmt.setInt(5, course.getSemester());
            stmt.setString(6, course.getDescription());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_code=?, course_name=?, credits=?, department_id=?, " +
                     "semester=?, description=? WHERE id=?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getDepartmentId());
            stmt.setInt(5, course.getSemester());
            stmt.setString(6, course.getDescription());
            stmt.setInt(7, course.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM courses WHERE is_active = TRUE";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<Course> search(String query) throws SQLException {
        String sql = "SELECT c.*, d.department_name, d.department_code " +
                     "FROM courses c LEFT JOIN departments d ON c.department_id = d.id " +
                     "WHERE c.course_code LIKE ? OR c.course_name LIKE ? " +
                     "ORDER BY c.course_code LIMIT 50";
        List<Course> courses = new ArrayList<>();
        String pattern = "%" + query + "%";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) courses.add(mapResultSet(rs));
            }
        }
        return courses;
    }

    private Course mapResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        course.setDepartmentId(rs.getInt("department_id"));
        course.setSemester(rs.getInt("semester"));
        course.setDescription(rs.getString("description"));
        course.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) course.setCreatedAt(createdAt.toLocalDateTime());

        try {
            course.setDepartmentName(rs.getString("department_name"));
            course.setDepartmentCode(rs.getString("department_code"));
        } catch (SQLException e) { /* Not always present */ }

        return course;
    }
}
