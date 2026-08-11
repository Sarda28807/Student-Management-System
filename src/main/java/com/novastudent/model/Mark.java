package com.novastudent.model;

import java.time.LocalDateTime;

/**
 * Represents marks/grades for a student in a course.
 * Maps to the 'marks' table.
 */
public class Mark {

    private int id;
    private int studentId;
    private int courseId;
    private double internalMarks;
    private double externalMarks;
    private double totalMarks;
    private String grade;
    private int semester;
    private String academicYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined fields
    private String studentName;
    private String studentCode;
    private String courseName;
    private String courseCode;
    private int courseCredits;
    private String departmentName;

    public Mark() {}

    public Mark(int studentId, int courseId, double internalMarks, double externalMarks, int semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.internalMarks = internalMarks;
        this.externalMarks = externalMarks;
        this.totalMarks = internalMarks + externalMarks;
        this.semester = semester;
        this.grade = calculateGrade(this.totalMarks);
    }

    // ========== Business Methods ==========

    /**
     * Calculates grade based on total marks (out of 100).
     */
    public static String calculateGrade(double total) {
        if (total >= 90) return "O";
        if (total >= 80) return "A+";
        if (total >= 70) return "A";
        if (total >= 60) return "B+";
        if (total >= 50) return "B";
        if (total >= 40) return "C";
        if (total >= 30) return "P";
        return "F";
    }

    /**
     * Returns grade point for GPA calculation.
     */
    public double getGradePoint() {
        if (grade == null) return 0.0;
        switch (grade) {
            case "O":  return 10.0;
            case "A+": return 9.0;
            case "A":  return 8.0;
            case "B+": return 7.0;
            case "B":  return 6.0;
            case "C":  return 5.0;
            case "P":  return 4.0;
            case "F":  return 0.0;
            default:   return 0.0;
        }
    }

    /**
     * Returns percentage.
     */
    public double getPercentage() {
        return totalMarks;
    }

    /**
     * Recalculates total and grade from internal/external marks.
     */
    public void recalculate() {
        this.totalMarks = this.internalMarks + this.externalMarks;
        this.grade = calculateGrade(this.totalMarks);
    }

    // ========== Getters and Setters ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public double getInternalMarks() { return internalMarks; }
    public void setInternalMarks(double internalMarks) {
        this.internalMarks = internalMarks;
        recalculate();
    }

    public double getExternalMarks() { return externalMarks; }
    public void setExternalMarks(double externalMarks) {
        this.externalMarks = externalMarks;
        recalculate();
    }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public int getCourseCredits() { return courseCredits; }
    public void setCourseCredits(int courseCredits) { this.courseCredits = courseCredits; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    @Override
    public String toString() {
        return "Mark{" +
                "student=" + studentCode +
                ", course=" + courseCode +
                ", total=" + totalMarks +
                ", grade='" + grade + '\'' +
                '}';
    }
}
