package com.novastudent.dao;

import com.novastudent.database.DatabaseConnection;
import com.novastudent.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student entity.
 * Handles all student-related database operations using PreparedStatement.
 */
public class StudentDAO {

    private final DatabaseConnection dbConnection;

    public StudentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Retrieves all students with department info, with optional pagination.
     */
    public List<Student> getAllStudents(int limit, int offset) throws SQLException {
        String sql = "SELECT s.*, d.department_name, d.department_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "ORDER BY s.created_at DESC LIMIT ? OFFSET ?";
        List<Student> students = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSet(rs));
                }
            }
        }
        return students;
    }

    /**
     * Retrieves all students (no pagination).
     */
    public List<Student> getAllStudents() throws SQLException {
        return getAllStudents(1000, 0);
    }

    /**
     * Finds a student by database ID.
     */
    public Student findById(int id) throws SQLException {
        String sql = "SELECT s.*, d.department_name, d.department_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "WHERE s.id = ?";
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
     * Finds a student by unique student ID (e.g., CSE2023001).
     */
    public Student findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT s.*, d.department_name, d.department_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "WHERE s.student_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Checks if a student ID already exists.
     */
    public boolean studentIdExists(String studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Checks if an email already exists (optionally excluding a student).
     */
    public boolean emailExists(String email, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ? AND id != ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Inserts a new student into the database.
     *
     * @return the auto-generated ID
     */
    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_id, first_name, last_name, email, phone, " +
                     "gender, date_of_birth, department_id, year, semester, address, " +
                     "enrollment_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getGender().name());
            stmt.setDate(7, student.getDateOfBirth() != null ? Date.valueOf(student.getDateOfBirth()) : null);
            stmt.setInt(8, student.getDepartmentId());
            stmt.setInt(9, student.getYear());
            stmt.setInt(10, student.getSemester());
            stmt.setString(11, student.getAddress());
            stmt.setDate(12, Date.valueOf(student.getEnrollmentDate()));
            stmt.setString(13, student.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Updates an existing student.
     */
    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, " +
                     "gender=?, date_of_birth=?, department_id=?, year=?, semester=?, " +
                     "address=?, status=? WHERE id=?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setString(5, student.getGender().name());
            stmt.setDate(6, student.getDateOfBirth() != null ? Date.valueOf(student.getDateOfBirth()) : null);
            stmt.setInt(7, student.getDepartmentId());
            stmt.setInt(8, student.getYear());
            stmt.setInt(9, student.getSemester());
            stmt.setString(10, student.getAddress());
            stmt.setString(11, student.getStatus().name());
            stmt.setInt(12, student.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a student by database ID.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Searches students by name, student ID, or email.
     */
    public List<Student> search(String query) throws SQLException {
        String sql = "SELECT s.*, d.department_name, d.department_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "WHERE s.student_id LIKE ? OR s.first_name LIKE ? OR s.last_name LIKE ? " +
                     "OR s.email LIKE ? OR CONCAT(s.first_name, ' ', s.last_name) LIKE ? " +
                     "ORDER BY s.first_name LIMIT 50";
        List<Student> students = new ArrayList<>();
        String pattern = "%" + query + "%";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSet(rs));
                }
            }
        }
        return students;
    }

    /**
     * Filters students by department, year, and/or status.
     */
    public List<Student> filter(Integer departmentId, Integer year, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, d.department_name, d.department_code " +
            "FROM students s " +
            "LEFT JOIN departments d ON s.department_id = d.id WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (departmentId != null && departmentId > 0) {
            sql.append(" AND s.department_id = ?");
            params.add(departmentId);
        }
        if (year != null && year > 0) {
            sql.append(" AND s.year = ?");
            params.add(year);
        }
        if (status != null && !status.isEmpty() && !status.equals("All")) {
            sql.append(" AND s.status = ?");
            params.add(status);
        }

        sql.append(" ORDER BY s.created_at DESC LIMIT 500");

        List<Student> students = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSet(rs));
                }
            }
        }
        return students;
    }

    /**
     * Returns the total number of students.
     */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Returns count of students by status.
     */
    public int getCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE status = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Returns recent students (last N added).
     */
    public List<Student> getRecentStudents(int limit) throws SQLException {
        return getAllStudents(limit, 0);
    }

    /**
     * Maps a ResultSet row to a Student object.
     */
    private Student mapResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setGender(Student.Gender.valueOf(rs.getString("gender")));

        Date dob = rs.getDate("date_of_birth");
        student.setDateOfBirth(dob != null ? dob.toLocalDate() : null);

        student.setDepartmentId(rs.getInt("department_id"));
        student.setYear(rs.getInt("year"));
        student.setSemester(rs.getInt("semester"));
        student.setAddress(rs.getString("address"));

        Date enrollDate = rs.getDate("enrollment_date");
        student.setEnrollmentDate(enrollDate != null ? enrollDate.toLocalDate() : null);

        student.setStatus(Student.Status.valueOf(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) student.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) student.setUpdatedAt(updatedAt.toLocalDateTime());

        // Join fields
        try {
            student.setDepartmentName(rs.getString("department_name"));
            student.setDepartmentCode(rs.getString("department_code"));
        } catch (SQLException e) {
            // Fields not present in all queries
        }

        return student;
    }
}
