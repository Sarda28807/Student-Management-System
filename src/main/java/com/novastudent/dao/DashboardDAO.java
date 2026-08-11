package com.novastudent.dao;

import com.novastudent.database.DatabaseConnection;
import com.novastudent.model.DashboardStats;
import com.novastudent.model.Student;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Access Object for dashboard aggregate statistics.
 * Combines data from multiple tables for the dashboard view.
 */
public class DashboardDAO {

    private final DatabaseConnection dbConnection;
    private final StudentDAO studentDAO;
    private final AttendanceDAO attendanceDAO;
    private final MarkDAO markDAO;
    private final DepartmentDAO departmentDAO;
    private final CourseDAO courseDAO;

    public DashboardDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.studentDAO = new StudentDAO();
        this.attendanceDAO = new AttendanceDAO();
        this.markDAO = new MarkDAO();
        this.departmentDAO = new DepartmentDAO();
        this.courseDAO = new CourseDAO();
    }

    /**
     * Loads all dashboard statistics in a single method call.
     */
    public DashboardStats loadDashboardStats() throws SQLException {
        DashboardStats stats = new DashboardStats();

        stats.setTotalStudents(studentDAO.getTotalCount());
        stats.setActiveStudents(studentDAO.getCountByStatus("ACTIVE"));
        stats.setPresentToday(attendanceDAO.getPresentToday());
        stats.setAttendanceRate(attendanceDAO.getAttendanceRate());
        stats.setTotalCourses(courseDAO.getCount());
        stats.setTotalDepartments(departmentDAO.getCount());
        stats.setAverageGPA(markDAO.getAverageGPA());
        stats.setGraduatedStudents(studentDAO.getCountByStatus("GRADUATED"));
        stats.setNewStudentsThisMonth(getNewStudentsThisMonth());
        stats.setStudentGrowthPercent(calculateGrowthPercent());
        stats.setDepartmentDistribution(getDepartmentDistribution());
        stats.setAttendanceTrend(attendanceDAO.getAttendanceTrend(7));
        stats.setGradeDistribution(markDAO.getGradeDistribution());
        stats.setTopPerformers(markDAO.getTopPerformers(5));
        stats.setRecentStudents(studentDAO.getRecentStudents(5));

        return stats;
    }

    /**
     * Gets count of students enrolled this month.
     */
    private int getNewStudentsThisMonth() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Calculates student growth percentage compared to previous month.
     */
    private double calculateGrowthPercent() throws SQLException {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM students WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())) AS this_month, " +
                     "(SELECT COUNT(*) FROM students WHERE MONTH(created_at) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) " +
                     "AND YEAR(created_at) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))) AS last_month";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int thisMonth = rs.getInt("this_month");
                int lastMonth = rs.getInt("last_month");
                if (lastMonth > 0) {
                    return Math.round((thisMonth - lastMonth) * 100.0 / lastMonth * 10.0) / 10.0;
                }
                return thisMonth > 0 ? 100.0 : 0.0;
            }
        }
        return 0.0;
    }

    /**
     * Gets student count per department.
     */
    private Map<String, Integer> getDepartmentDistribution() throws SQLException {
        String sql = "SELECT d.department_code, COUNT(s.id) AS student_count " +
                     "FROM departments d " +
                     "LEFT JOIN students s ON d.id = s.department_id AND s.status = 'ACTIVE' " +
                     "WHERE d.is_active = TRUE " +
                     "GROUP BY d.id, d.department_code ORDER BY student_count DESC";
        Map<String, Integer> dist = new LinkedHashMap<>();
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dist.put(rs.getString("department_code"), rs.getInt("student_count"));
            }
        }
        return dist;
    }
}
