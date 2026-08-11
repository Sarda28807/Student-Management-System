package com.novastudent.dao;

import com.novastudent.database.DatabaseConnection;
import com.novastudent.model.Attendance;
import com.novastudent.model.Attendance.AttendanceStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Attendance entity.
 * Includes duplicate prevention via unique constraint.
 */
public class AttendanceDAO {

    private final DatabaseConnection dbConnection;

    public AttendanceDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Gets all attendance records with student and course details.
     */
    public List<Attendance> getAllAttendance(int limit, int offset) throws SQLException {
        String sql = "SELECT a.*, " +
                     "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                     "s.student_id AS student_code, " +
                     "c.course_name, c.course_code, " +
                     "d.department_name " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN courses c ON a.course_id = c.id " +
                     "JOIN departments d ON s.department_id = d.id " +
                     "ORDER BY a.attendance_date DESC, s.first_name LIMIT ? OFFSET ?";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Gets attendance for a specific student.
     */
    public List<Attendance> getByStudent(int studentDbId) throws SQLException {
        String sql = "SELECT a.*, " +
                     "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                     "s.student_id AS student_code, " +
                     "c.course_name, c.course_code, " +
                     "d.department_name " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN courses c ON a.course_id = c.id " +
                     "JOIN departments d ON s.department_id = d.id " +
                     "WHERE a.student_id = ? ORDER BY a.attendance_date DESC";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentDbId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Gets attendance filtered by date, department, course, and/or status.
     */
    public List<Attendance> filter(LocalDate date, Integer departmentId, Integer courseId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT a.*, " +
            "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
            "s.student_id AS student_code, " +
            "c.course_name, c.course_code, " +
            "d.department_name " +
            "FROM attendance a " +
            "JOIN students s ON a.student_id = s.id " +
            "JOIN courses c ON a.course_id = c.id " +
            "JOIN departments d ON s.department_id = d.id WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (date != null) {
            sql.append(" AND a.attendance_date = ?");
            params.add(Date.valueOf(date));
        }
        if (departmentId != null && departmentId > 0) {
            sql.append(" AND s.department_id = ?");
            params.add(departmentId);
        }
        if (courseId != null && courseId > 0) {
            sql.append(" AND a.course_id = ?");
            params.add(courseId);
        }
        if (status != null && !status.isEmpty() && !status.equals("All")) {
            sql.append(" AND a.status = ?");
            params.add(status);
        }

        sql.append(" ORDER BY a.attendance_date DESC, s.first_name LIMIT 500");

        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) stmt.setInt(i + 1, (Integer) p);
                else if (p instanceof Date) stmt.setDate(i + 1, (Date) p);
                else stmt.setString(i + 1, p.toString());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Marks attendance for a student. Uses INSERT ... ON DUPLICATE KEY UPDATE
     * to prevent duplicate entries for the same student/course/date.
     */
    public boolean markAttendance(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, course_id, attendance_date, status, remarks) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), remarks = VALUES(remarks)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getCourseId());
            stmt.setDate(3, Date.valueOf(attendance.getAttendanceDate()));
            stmt.setString(4, attendance.getStatus().name());
            stmt.setString(5, attendance.getRemarks());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Bulk mark attendance for multiple students.
     */
    public int bulkMarkAttendance(List<Attendance> attendanceList) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, course_id, attendance_date, status, remarks) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), remarks = VALUES(remarks)";
        int count = 0;
        Connection conn = dbConnection.getConnection();
        conn.setAutoCommit(false);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Attendance a : attendanceList) {
                stmt.setInt(1, a.getStudentId());
                stmt.setInt(2, a.getCourseId());
                stmt.setDate(3, Date.valueOf(a.getAttendanceDate()));
                stmt.setString(4, a.getStatus().name());
                stmt.setString(5, a.getRemarks());
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            conn.commit();
            for (int r : results) if (r > 0) count++;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        return count;
    }

    /**
     * Deletes an attendance record.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Gets attendance count for today.
     */
    public int getPresentToday() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM attendance WHERE attendance_date = CURDATE() AND status = 'PRESENT'";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Gets overall attendance rate as percentage.
     */
    public double getAttendanceRate() throws SQLException {
        String sql = "SELECT " +
                     "COUNT(CASE WHEN status = 'PRESENT' THEN 1 END) * 100.0 / NULLIF(COUNT(*), 0) " +
                     "FROM attendance WHERE attendance_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    /**
     * Gets attendance trend for the last N days.
     * Returns map of date -> [present, absent, late]
     */
    public java.util.Map<String, int[]> getAttendanceTrend(int days) throws SQLException {
        String sql = "SELECT attendance_date, " +
                     "SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, " +
                     "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count, " +
                     "SUM(CASE WHEN status = 'LATE' THEN 1 ELSE 0 END) AS late_count " +
                     "FROM attendance " +
                     "WHERE attendance_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                     "GROUP BY attendance_date ORDER BY attendance_date";
        java.util.Map<String, int[]> trend = new java.util.LinkedHashMap<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getDate("attendance_date").toString();
                    trend.put(date, new int[]{
                        rs.getInt("present_count"),
                        rs.getInt("absent_count"),
                        rs.getInt("late_count")
                    });
                }
            }
        }
        return trend;
    }

    /**
     * Gets attendance statistics for a specific student.
     * Returns [total, present, absent, late]
     */
    public int[] getStudentAttendanceStats(int studentDbId) throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present, " +
                     "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) AS absent, " +
                     "SUM(CASE WHEN status = 'LATE' THEN 1 ELSE 0 END) AS late_count " +
                     "FROM attendance WHERE student_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentDbId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                        rs.getInt("total"),
                        rs.getInt("present"),
                        rs.getInt("absent"),
                        rs.getInt("late_count")
                    };
                }
            }
        }
        return new int[]{0, 0, 0, 0};
    }

    private Attendance mapResultSet(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setCourseId(rs.getInt("course_id"));
        a.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        a.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
        a.setRemarks(rs.getString("remarks"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) a.setCreatedAt(createdAt.toLocalDateTime());

        try {
            a.setStudentName(rs.getString("student_name"));
            a.setStudentCode(rs.getString("student_code"));
            a.setCourseName(rs.getString("course_name"));
            a.setCourseCode(rs.getString("course_code"));
            a.setDepartmentName(rs.getString("department_name"));
        } catch (SQLException e) { /* Not always present */ }

        return a;
    }
}
