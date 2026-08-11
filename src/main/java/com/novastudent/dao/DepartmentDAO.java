package com.novastudent.dao;

import com.novastudent.database.DatabaseConnection;
import com.novastudent.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Department entity.
 */
public class DepartmentDAO {

    private final DatabaseConnection dbConnection;

    public DepartmentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Retrieves all departments with student and course counts.
     */
    public List<Department> getAllDepartments() throws SQLException {
        String sql = "SELECT d.*, " +
                     "(SELECT COUNT(*) FROM students s WHERE s.department_id = d.id) AS student_count, " +
                     "(SELECT COUNT(*) FROM courses c WHERE c.department_id = d.id) AS course_count " +
                     "FROM departments d WHERE d.is_active = TRUE ORDER BY d.department_code";
        List<Department> departments = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(mapResultSet(rs));
            }
        }
        return departments;
    }

    /**
     * Finds a department by ID.
     */
    public Department findById(int id) throws SQLException {
        String sql = "SELECT d.*, " +
                     "(SELECT COUNT(*) FROM students s WHERE s.department_id = d.id) AS student_count, " +
                     "(SELECT COUNT(*) FROM courses c WHERE c.department_id = d.id) AS course_count " +
                     "FROM departments d WHERE d.id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserts a new department.
     */
    public int insert(Department dept) throws SQLException {
        String sql = "INSERT INTO departments (department_code, department_name, hod_name, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dept.getDepartmentCode());
            stmt.setString(2, dept.getDepartmentName());
            stmt.setString(3, dept.getHodName());
            stmt.setString(4, dept.getDescription());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Updates a department.
     */
    public boolean update(Department dept) throws SQLException {
        String sql = "UPDATE departments SET department_code=?, department_name=?, hod_name=?, description=? WHERE id=?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, dept.getDepartmentCode());
            stmt.setString(2, dept.getDepartmentName());
            stmt.setString(3, dept.getHodName());
            stmt.setString(4, dept.getDescription());
            stmt.setInt(5, dept.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a department.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Gets count of all active departments.
     */
    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM departments WHERE is_active = TRUE";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Department mapResultSet(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setDepartmentCode(rs.getString("department_code"));
        dept.setDepartmentName(rs.getString("department_name"));
        dept.setHodName(rs.getString("hod_name"));
        dept.setDescription(rs.getString("description"));
        dept.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) dept.setCreatedAt(createdAt.toLocalDateTime());

        try {
            dept.setStudentCount(rs.getInt("student_count"));
            dept.setCourseCount(rs.getInt("course_count"));
        } catch (SQLException e) { /* Not always present */ }

        return dept;
    }
}
