package com.novastudent.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a student entity in the NovaStudent system.
 * Maps to the 'students' table in the database.
 *
 * @author NovaStudent
 * @version 1.0.0
 */
public class Student {

    /** Database auto-increment ID */
    private int id;

    /** Unique student identifier (e.g., CSE2023001) */
    private String studentId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private int year;
    private int semester;
    private String address;
    private LocalDate enrollmentDate;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Student status enum */
    public enum Status {
        ACTIVE, INACTIVE, GRADUATED, SUSPENDED
    }

    /** Gender enum */
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    // ========== Constructors ==========

    public Student() {
        this.status = Status.ACTIVE;
        this.enrollmentDate = LocalDate.now();
    }

    public Student(String studentId, String firstName, String lastName, String email,
                   String phone, Gender gender, LocalDate dateOfBirth,
                   int departmentId, int year, int semester) {
        this();
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.departmentId = departmentId;
        this.year = year;
        this.semester = semester;
    }

    // ========== Business Methods ==========

    /**
     * Returns the full name of the student.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns initials for avatar display.
     */
    public String getInitials() {
        String initials = "";
        if (firstName != null && !firstName.isEmpty()) {
            initials += firstName.charAt(0);
        }
        if (lastName != null && !lastName.isEmpty()) {
            initials += lastName.charAt(0);
        }
        return initials.toUpperCase();
    }

    // ========== Getters and Setters ==========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", department=" + departmentCode +
                ", year=" + year +
                ", status=" + status +
                '}';
    }
}
