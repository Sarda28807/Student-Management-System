package com.novastudent.model;

import java.time.LocalDateTime;

/**
 * Represents a department in the university.
 * Maps to the 'departments' table.
 */
public class Department {

    private int id;
    private String departmentCode;
    private String departmentName;
    private String hodName;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    // Additional computed fields
    private int studentCount;
    private int courseCount;

    public Department() {
        this.active = true;
    }

    public Department(String departmentCode, String departmentName, String hodName) {
        this();
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.hodName = hodName;
    }

    // ========== Getters and Setters ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getHodName() { return hodName; }
    public void setHodName(String hodName) { this.hodName = hodName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getCourseCount() { return courseCount; }
    public void setCourseCount(int courseCount) { this.courseCount = courseCount; }

    @Override
    public String toString() {
        return departmentCode + " - " + departmentName;
    }
}
