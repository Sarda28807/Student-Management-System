package com.novastudent.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an attendance record for a student in a course.
 * Maps to the 'attendance' table.
 */
public class Attendance {

    private int id;
    private int studentId;
    private int courseId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String remarks;
    private LocalDateTime createdAt;

    // Joined fields for display
    private String studentName;
    private String studentCode;
    private String courseName;
    private String courseCode;
    private String departmentName;

    /** Attendance status enum */
    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE
    }

    public Attendance() {
        this.attendanceDate = LocalDate.now();
        this.status = AttendanceStatus.PRESENT;
    }

    public Attendance(int studentId, int courseId, LocalDate attendanceDate, AttendanceStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    // ========== Getters and Setters ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    @Override
    public String toString() {
        return "Attendance{" +
                "studentId=" + studentId +
                ", courseId=" + courseId +
                ", date=" + attendanceDate +
                ", status=" + status +
                '}';
    }
}
