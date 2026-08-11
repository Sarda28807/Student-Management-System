package com.novastudent.service;

import com.novastudent.dao.MarkDAO;
import com.novastudent.model.Mark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service layer for academic results and grade management.
 */
public class ResultService {

    private final MarkDAO markDAO;

    public ResultService() {
        this.markDAO = new MarkDAO();
    }

    public List<Mark> getAllMarks(int limit, int offset) throws SQLException {
        return markDAO.getAllMarks(limit, offset);
    }

    public List<Mark> getStudentMarks(int studentDbId) throws SQLException {
        return markDAO.getByStudent(studentDbId);
    }

    public List<Mark> filterMarks(Integer semester, Integer departmentId, Integer courseId) throws SQLException {
        return markDAO.filter(semester, departmentId, courseId);
    }

    /**
     * Saves marks with validation.
     * Automatically calculates total and grade.
     */
    public List<String> saveMarks(Mark mark) throws SQLException {
        List<String> errors = validateMarks(mark);
        if (!errors.isEmpty()) return errors;

        mark.recalculate();

        if (markDAO.insertOrUpdate(mark)) {
            return new ArrayList<>();
        }
        errors.add("Failed to save marks");
        return errors;
    }

    public boolean deleteMarks(int id) throws SQLException {
        return markDAO.delete(id);
    }

    public double calculateGPA(int studentDbId) throws SQLException {
        return markDAO.calculateGPA(studentDbId);
    }

    public double getAverageGPA() throws SQLException {
        return markDAO.getAverageGPA();
    }

    public Map<String, Integer> getGradeDistribution() throws SQLException {
        return markDAO.getGradeDistribution();
    }

    public List<String[]> getTopPerformers(int limit) throws SQLException {
        return markDAO.getTopPerformers(limit);
    }

    public int getTotalCredits(int studentDbId) throws SQLException {
        return markDAO.getTotalCredits(studentDbId);
    }

    private List<String> validateMarks(Mark mark) {
        List<String> errors = new ArrayList<>();
        if (mark.getStudentId() <= 0) errors.add("Student is required");
        if (mark.getCourseId() <= 0) errors.add("Course is required");
        if (mark.getInternalMarks() < 0 || mark.getInternalMarks() > 40)
            errors.add("Internal marks must be between 0 and 40");
        if (mark.getExternalMarks() < 0 || mark.getExternalMarks() > 60)
            errors.add("External marks must be between 0 and 60");
        if (mark.getSemester() < 1 || mark.getSemester() > 8)
            errors.add("Semester must be between 1 and 8");
        return errors;
    }
}
