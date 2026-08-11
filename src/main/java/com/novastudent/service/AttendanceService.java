package com.novastudent.service;

import com.novastudent.dao.AttendanceDAO;
import com.novastudent.model.Attendance;
import com.novastudent.model.Attendance.AttendanceStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service layer for attendance operations.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public List<Attendance> getAllAttendance(int limit, int offset) throws SQLException {
        return attendanceDAO.getAllAttendance(limit, offset);
    }

    public List<Attendance> getStudentAttendance(int studentDbId) throws SQLException {
        return attendanceDAO.getByStudent(studentDbId);
    }

    public List<Attendance> filterAttendance(LocalDate date, Integer departmentId, Integer courseId, String status) throws SQLException {
        return attendanceDAO.filter(date, departmentId, courseId, status);
    }

    public boolean markAttendance(int studentId, int courseId, LocalDate date, AttendanceStatus status) throws SQLException {
        Attendance attendance = new Attendance(studentId, courseId, date, status);
        return attendanceDAO.markAttendance(attendance);
    }

    public int bulkMarkAttendance(List<Attendance> attendanceList) throws SQLException {
        return attendanceDAO.bulkMarkAttendance(attendanceList);
    }

    public boolean deleteAttendance(int id) throws SQLException {
        return attendanceDAO.delete(id);
    }

    public int getPresentToday() throws SQLException {
        return attendanceDAO.getPresentToday();
    }

    public double getAttendanceRate() throws SQLException {
        return attendanceDAO.getAttendanceRate();
    }

    public Map<String, int[]> getAttendanceTrend(int days) throws SQLException {
        return attendanceDAO.getAttendanceTrend(days);
    }

    public int[] getStudentAttendanceStats(int studentDbId) throws SQLException {
        return attendanceDAO.getStudentAttendanceStats(studentDbId);
    }
}
