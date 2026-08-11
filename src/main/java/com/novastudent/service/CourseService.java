package com.novastudent.service;

import com.novastudent.dao.CourseDAO;
import com.novastudent.dao.DepartmentDAO;
import com.novastudent.model.Course;
import com.novastudent.model.Department;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for course and department management.
 */
public class CourseService {

    private final CourseDAO courseDAO;
    private final DepartmentDAO departmentDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.departmentDAO = new DepartmentDAO();
    }

    // ===== Course Operations =====

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.getAllCourses();
    }

    public List<Course> getCoursesByDepartment(int departmentId) throws SQLException {
        return courseDAO.getByDepartment(departmentId);
    }

    public List<Course> getCoursesBySemester(int semester) throws SQLException {
        return courseDAO.getBySemester(semester);
    }

    public Course getCourseById(int id) throws SQLException {
        return courseDAO.findById(id);
    }

    public List<String> createCourse(Course course) throws SQLException {
        List<String> errors = validateCourse(course);
        if (!errors.isEmpty()) return errors;

        int id = courseDAO.insert(course);
        if (id > 0) {
            course.setId(id);
            return new ArrayList<>();
        }
        errors.add("Failed to create course");
        return errors;
    }

    public List<String> updateCourse(Course course) throws SQLException {
        List<String> errors = validateCourse(course);
        if (!errors.isEmpty()) return errors;

        if (courseDAO.update(course)) return new ArrayList<>();
        errors.add("Failed to update course");
        return errors;
    }

    public boolean deleteCourse(int id) throws SQLException {
        return courseDAO.delete(id);
    }

    public List<Course> searchCourses(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) return courseDAO.getAllCourses();
        return courseDAO.search(query.trim());
    }

    private List<String> validateCourse(Course course) {
        List<String> errors = new ArrayList<>();
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty())
            errors.add("Course code is required");
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty())
            errors.add("Course name is required");
        if (course.getCredits() < 1 || course.getCredits() > 6)
            errors.add("Credits must be between 1 and 6");
        if (course.getDepartmentId() <= 0)
            errors.add("Department is required");
        if (course.getSemester() < 1 || course.getSemester() > 8)
            errors.add("Semester must be between 1 and 8");
        return errors;
    }

    // ===== Department Operations =====

    public List<Department> getAllDepartments() throws SQLException {
        return departmentDAO.getAllDepartments();
    }

    public Department getDepartmentById(int id) throws SQLException {
        return departmentDAO.findById(id);
    }

    public List<String> createDepartment(Department dept) throws SQLException {
        List<String> errors = new ArrayList<>();
        if (dept.getDepartmentCode() == null || dept.getDepartmentCode().trim().isEmpty())
            errors.add("Department code is required");
        if (dept.getDepartmentName() == null || dept.getDepartmentName().trim().isEmpty())
            errors.add("Department name is required");
        if (!errors.isEmpty()) return errors;

        int id = departmentDAO.insert(dept);
        if (id > 0) { dept.setId(id); return new ArrayList<>(); }
        errors.add("Failed to create department");
        return errors;
    }

    public boolean deleteDepartment(int id) throws SQLException {
        return departmentDAO.delete(id);
    }
}
