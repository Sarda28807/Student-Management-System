package com.novastudent.model;

import java.time.LocalDateTime;

/**
 * Represents a course offered by a department.
 * Maps to the 'courses' table.
 */
public class Course {

    private int id;
    private String courseCode;
    private String courseName;
    private int credits;
    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private int semester;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    // Computed
    private int enrolledStudents;

    public Course() {
        this.active = true;
    }

    public Course(String courseCode, String courseName, int credits, int departmentId, int semester) {
        this();
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.departmentId = departmentId;
        this.semester = semester;
    }

    // ========== Getters and Setters ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(int enrolledStudents) { this.enrolledStudents = enrolledStudents; }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
