package com.novastudent.model;

/**
 * Data Transfer Object for dashboard statistics.
 * Aggregated from multiple database queries.
 */
public class DashboardStats {

    private int totalStudents;
    private int activeStudents;
    private int presentToday;
    private double attendanceRate;
    private int totalCourses;
    private int totalDepartments;
    private double averageGPA;
    private int graduatedStudents;
    private int newStudentsThisMonth;
    private double studentGrowthPercent;

    // Department distribution
    private java.util.Map<String, Integer> departmentDistribution;

    // Attendance trend (last 7 days)
    private java.util.Map<String, int[]> attendanceTrend; // date -> [present, absent, late]

    // Grade distribution
    private java.util.Map<String, Integer> gradeDistribution;

    // Top performers
    private java.util.List<String[]> topPerformers; // [name, gpa]

    // Recent students
    private java.util.List<Student> recentStudents;

    public DashboardStats() {
        this.departmentDistribution = new java.util.LinkedHashMap<>();
        this.attendanceTrend = new java.util.LinkedHashMap<>();
        this.gradeDistribution = new java.util.LinkedHashMap<>();
        this.topPerformers = new java.util.ArrayList<>();
        this.recentStudents = new java.util.ArrayList<>();
    }

    // ========== Getters and Setters ==========

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getActiveStudents() { return activeStudents; }
    public void setActiveStudents(int activeStudents) { this.activeStudents = activeStudents; }

    public int getPresentToday() { return presentToday; }
    public void setPresentToday(int presentToday) { this.presentToday = presentToday; }

    public double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(double attendanceRate) { this.attendanceRate = attendanceRate; }

    public int getTotalCourses() { return totalCourses; }
    public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }

    public int getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(int totalDepartments) { this.totalDepartments = totalDepartments; }

    public double getAverageGPA() { return averageGPA; }
    public void setAverageGPA(double averageGPA) { this.averageGPA = averageGPA; }

    public int getGraduatedStudents() { return graduatedStudents; }
    public void setGraduatedStudents(int graduatedStudents) { this.graduatedStudents = graduatedStudents; }

    public int getNewStudentsThisMonth() { return newStudentsThisMonth; }
    public void setNewStudentsThisMonth(int newStudentsThisMonth) { this.newStudentsThisMonth = newStudentsThisMonth; }

    public double getStudentGrowthPercent() { return studentGrowthPercent; }
    public void setStudentGrowthPercent(double studentGrowthPercent) { this.studentGrowthPercent = studentGrowthPercent; }

    public java.util.Map<String, Integer> getDepartmentDistribution() { return departmentDistribution; }
    public void setDepartmentDistribution(java.util.Map<String, Integer> departmentDistribution) { this.departmentDistribution = departmentDistribution; }

    public java.util.Map<String, int[]> getAttendanceTrend() { return attendanceTrend; }
    public void setAttendanceTrend(java.util.Map<String, int[]> attendanceTrend) { this.attendanceTrend = attendanceTrend; }

    public java.util.Map<String, Integer> getGradeDistribution() { return gradeDistribution; }
    public void setGradeDistribution(java.util.Map<String, Integer> gradeDistribution) { this.gradeDistribution = gradeDistribution; }

    public java.util.List<String[]> getTopPerformers() { return topPerformers; }
    public void setTopPerformers(java.util.List<String[]> topPerformers) { this.topPerformers = topPerformers; }

    public java.util.List<Student> getRecentStudents() { return recentStudents; }
    public void setRecentStudents(java.util.List<Student> recentStudents) { this.recentStudents = recentStudents; }
}
