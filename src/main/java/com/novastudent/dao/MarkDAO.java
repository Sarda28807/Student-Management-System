package com.novastudent.dao;

import com.novastudent.database.DatabaseConnection;
import com.novastudent.model.Mark;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Mark/Result entity.
 */
public class MarkDAO {

    private final DatabaseConnection dbConnection;

    public MarkDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Gets all marks with student and course info.
     */
    public List<Mark> getAllMarks(int limit, int offset) throws SQLException {
        String sql = "SELECT m.*, " +
                     "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                     "s.student_id AS student_code, " +
                     "c.course_name, c.course_code, c.credits AS course_credits, " +
                     "d.department_name " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN courses c ON m.course_id = c.id " +
                     "JOIN departments d ON s.department_id = d.id " +
                     "ORDER BY s.student_id, m.semester LIMIT ? OFFSET ?";
        List<Mark> marks = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) marks.add(mapResultSet(rs));
            }
        }
        return marks;
    }

    /**
     * Gets marks for a specific student.
     */
    public List<Mark> getByStudent(int studentDbId) throws SQLException {
        String sql = "SELECT m.*, " +
                     "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                     "s.student_id AS student_code, " +
                     "c.course_name, c.course_code, c.credits AS course_credits, " +
                     "d.department_name " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN courses c ON m.course_id = c.id " +
                     "JOIN departments d ON s.department_id = d.id " +
                     "WHERE m.student_id = ? ORDER BY m.semester, c.course_code";
        List<Mark> marks = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentDbId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) marks.add(mapResultSet(rs));
            }
        }
        return marks;
    }

    /**
     * Filters marks by semester, department, and/or course.
     */
    public List<Mark> filter(Integer semester, Integer departmentId, Integer courseId) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, " +
            "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
            "s.student_id AS student_code, " +
            "c.course_name, c.course_code, c.credits AS course_credits, " +
            "d.department_name " +
            "FROM marks m " +
            "JOIN students s ON m.student_id = s.id " +
            "JOIN courses c ON m.course_id = c.id " +
            "JOIN departments d ON s.department_id = d.id WHERE 1=1");

        List<Object> params = new ArrayList<>();
        if (semester != null && semester > 0) {
            sql.append(" AND m.semester = ?");
            params.add(semester);
        }
        if (departmentId != null && departmentId > 0) {
            sql.append(" AND s.department_id = ?");
            params.add(departmentId);
        }
        if (courseId != null && courseId > 0) {
            sql.append(" AND m.course_id = ?");
            params.add(courseId);
        }
        sql.append(" ORDER BY s.student_id, m.semester LIMIT 500");

        List<Mark> marks = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setInt(i + 1, (Integer) params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) marks.add(mapResultSet(rs));
            }
        }
        return marks;
    }

    /**
     * Inserts or updates marks (upsert on student+course+semester).
     */
    public boolean insertOrUpdate(Mark mark) throws SQLException {
        String sql = "INSERT INTO marks (student_id, course_id, internal_marks, external_marks, semester, academic_year) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE internal_marks = VALUES(internal_marks), " +
                     "external_marks = VALUES(external_marks), academic_year = VALUES(academic_year)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, mark.getStudentId());
            stmt.setInt(2, mark.getCourseId());
            stmt.setDouble(3, mark.getInternalMarks());
            stmt.setDouble(4, mark.getExternalMarks());
            stmt.setInt(5, mark.getSemester());
            stmt.setString(6, mark.getAcademicYear());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a mark record.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM marks WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Calculates GPA for a student (weighted by credits).
     */
    public double calculateGPA(int studentDbId) throws SQLException {
        String sql = "SELECT m.grade, c.credits FROM marks m " +
                     "JOIN courses c ON m.course_id = c.id " +
                     "WHERE m.student_id = ?";
        double totalGradePoints = 0;
        int totalCredits = 0;
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentDbId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String grade = rs.getString("grade");
                    int credits = rs.getInt("credits");
                    double gp = gradeToPoint(grade);
                    totalGradePoints += gp * credits;
                    totalCredits += credits;
                }
            }
        }
        return totalCredits > 0 ? Math.round(totalGradePoints / totalCredits * 100.0) / 100.0 : 0.0;
    }

    /**
     * Gets average GPA across all students.
     */
    public double getAverageGPA() throws SQLException {
        String sql = "SELECT AVG(gpa) FROM (" +
                     "  SELECT m.student_id, " +
                     "  SUM(CASE " +
                     "    WHEN m.grade = 'O' THEN 10 * c.credits " +
                     "    WHEN m.grade = 'A+' THEN 9 * c.credits " +
                     "    WHEN m.grade = 'A' THEN 8 * c.credits " +
                     "    WHEN m.grade = 'B+' THEN 7 * c.credits " +
                     "    WHEN m.grade = 'B' THEN 6 * c.credits " +
                     "    WHEN m.grade = 'C' THEN 5 * c.credits " +
                     "    WHEN m.grade = 'P' THEN 4 * c.credits " +
                     "    ELSE 0 END) / SUM(c.credits) AS gpa " +
                     "  FROM marks m JOIN courses c ON m.course_id = c.id " +
                     "  GROUP BY m.student_id" +
                     ") AS student_gpas";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return Math.round(rs.getDouble(1) * 100.0) / 100.0;
        }
        return 0.0;
    }

    /**
     * Gets grade distribution (count per grade).
     */
    public Map<String, Integer> getGradeDistribution() throws SQLException {
        String sql = "SELECT grade, COUNT(*) AS cnt FROM marks GROUP BY grade ORDER BY " +
                     "FIELD(grade, 'O', 'A+', 'A', 'B+', 'B', 'C', 'P', 'F')";
        Map<String, Integer> dist = new LinkedHashMap<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dist.put(rs.getString("grade"), rs.getInt("cnt"));
            }
        }
        return dist;
    }

    /**
     * Gets top N performers by GPA.
     * Returns list of [name, gpa]
     */
    public List<String[]> getTopPerformers(int limit) throws SQLException {
        String sql = "SELECT CONCAT(s.first_name, ' ', s.last_name) AS name, " +
                     "ROUND(SUM(CASE " +
                     "  WHEN m.grade = 'O' THEN 10 * c.credits " +
                     "  WHEN m.grade = 'A+' THEN 9 * c.credits " +
                     "  WHEN m.grade = 'A' THEN 8 * c.credits " +
                     "  WHEN m.grade = 'B+' THEN 7 * c.credits " +
                     "  WHEN m.grade = 'B' THEN 6 * c.credits " +
                     "  WHEN m.grade = 'C' THEN 5 * c.credits " +
                     "  WHEN m.grade = 'P' THEN 4 * c.credits " +
                     "  ELSE 0 END) / SUM(c.credits), 2) AS gpa " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN courses c ON m.course_id = c.id " +
                     "GROUP BY m.student_id, s.first_name, s.last_name " +
                     "ORDER BY gpa DESC LIMIT ?";
        List<String[]> performers = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    performers.add(new String[]{rs.getString("name"), String.valueOf(rs.getDouble("gpa"))});
                }
            }
        }
        return performers;
    }

    /**
     * Gets total credits earned by a student.
     */
    public int getTotalCredits(int studentDbId) throws SQLException {
        String sql = "SELECT SUM(c.credits) FROM marks m " +
                     "JOIN courses c ON m.course_id = c.id " +
                     "WHERE m.student_id = ? AND m.grade != 'F'";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentDbId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private double gradeToPoint(String grade) {
        if (grade == null) return 0;
        switch (grade) {
            case "O":  return 10.0;
            case "A+": return 9.0;
            case "A":  return 8.0;
            case "B+": return 7.0;
            case "B":  return 6.0;
            case "C":  return 5.0;
            case "P":  return 4.0;
            default:   return 0.0;
        }
    }

    private Mark mapResultSet(ResultSet rs) throws SQLException {
        Mark m = new Mark();
        m.setId(rs.getInt("id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setCourseId(rs.getInt("course_id"));
        m.setInternalMarks(rs.getDouble("internal_marks"));
        m.setExternalMarks(rs.getDouble("external_marks"));
        m.setTotalMarks(rs.getDouble("total_marks"));
        m.setGrade(rs.getString("grade"));
        m.setSemester(rs.getInt("semester"));
        m.setAcademicYear(rs.getString("academic_year"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) m.setCreatedAt(createdAt.toLocalDateTime());

        try {
            m.setStudentName(rs.getString("student_name"));
            m.setStudentCode(rs.getString("student_code"));
            m.setCourseName(rs.getString("course_name"));
            m.setCourseCode(rs.getString("course_code"));
            m.setCourseCredits(rs.getInt("course_credits"));
            m.setDepartmentName(rs.getString("department_name"));
        } catch (SQLException e) { /* Not always present */ }

        return m;
    }
}
