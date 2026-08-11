package com.novastudent.service;

import com.novastudent.dao.StudentDAO;
import com.novastudent.model.Student;
import com.novastudent.util.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for student business logic.
 * Validates data before passing to DAO layer.
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    /**
     * Gets all students with optional pagination.
     */
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    public List<Student> getAllStudents(int limit, int offset) throws SQLException {
        return studentDAO.getAllStudents(limit, offset);
    }

    /**
     * Gets a student by database ID.
     */
    public Student getById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    /**
     * Gets a student by student ID code.
     */
    public Student getByStudentId(String studentId) throws SQLException {
        return studentDAO.findByStudentId(studentId);
    }

    /**
     * Creates a new student after validation.
     *
     * @return List of validation errors (empty if successful)
     */
    public List<String> createStudent(Student student) throws SQLException {
        List<String> errors = validateStudent(student, true);
        if (!errors.isEmpty()) {
            return errors;
        }

        int id = studentDAO.insert(student);
        if (id > 0) {
            student.setId(id);
            return new ArrayList<>();
        }

        errors.add("Failed to create student. Please try again.");
        return errors;
    }

    /**
     * Updates an existing student after validation.
     */
    public List<String> updateStudent(Student student) throws SQLException {
        List<String> errors = validateStudent(student, false);
        if (!errors.isEmpty()) {
            return errors;
        }

        if (studentDAO.update(student)) {
            return new ArrayList<>();
        }

        errors.add("Failed to update student. Please try again.");
        return errors;
    }

    /**
     * Deletes a student by database ID.
     */
    public boolean deleteStudent(int id) throws SQLException {
        return studentDAO.delete(id);
    }

    /**
     * Searches students by query string.
     */
    public List<Student> searchStudents(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return studentDAO.getAllStudents();
        }
        return studentDAO.search(query.trim());
    }

    /**
     * Filters students by criteria.
     */
    public List<Student> filterStudents(Integer departmentId, Integer year, String status) throws SQLException {
        return studentDAO.filter(departmentId, year, status);
    }

    /**
     * Gets total student count.
     */
    public int getTotalCount() throws SQLException {
        return studentDAO.getTotalCount();
    }

    /**
     * Gets recent students.
     */
    public List<Student> getRecentStudents(int limit) throws SQLException {
        return studentDAO.getRecentStudents(limit);
    }

    /**
     * Validates student data.
     *
     * @param isNew true if creating a new student (checks ID uniqueness)
     * @return List of validation error messages
     */
    private List<String> validateStudent(Student student, boolean isNew) throws SQLException {
        List<String> errors = new ArrayList<>();

        if (ValidationUtil.isEmpty(student.getFirstName())) {
            errors.add("First name is required");
        }
        if (ValidationUtil.isEmpty(student.getLastName())) {
            errors.add("Last name is required");
        }
        if (ValidationUtil.isEmpty(student.getStudentId())) {
            errors.add("Student ID is required");
        }
        if (ValidationUtil.isEmpty(student.getEmail())) {
            errors.add("Email is required");
        } else if (!ValidationUtil.isValidEmail(student.getEmail())) {
            errors.add("Invalid email format");
        }
        if (student.getPhone() != null && !student.getPhone().isEmpty() && !ValidationUtil.isValidPhone(student.getPhone())) {
            errors.add("Invalid phone number format");
        }
        if (student.getGender() == null) {
            errors.add("Gender is required");
        }
        if (student.getDepartmentId() <= 0) {
            errors.add("Department is required");
        }
        if (student.getYear() < 1 || student.getYear() > 4) {
            errors.add("Year must be between 1 and 4");
        }
        if (student.getSemester() < 1 || student.getSemester() > 8) {
            errors.add("Semester must be between 1 and 8");
        }

        // Check uniqueness
        if (isNew && studentDAO.studentIdExists(student.getStudentId())) {
            errors.add("Student ID '" + student.getStudentId() + "' already exists");
        }
        if (studentDAO.emailExists(student.getEmail(), isNew ? 0 : student.getId())) {
            errors.add("Email '" + student.getEmail() + "' is already registered");
        }

        return errors;
    }
}
