package com.novastudent.service;

import com.novastudent.model.*;
import com.novastudent.util.CSVExporter;
import com.novastudent.util.PDFExporter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for report generation and data export.
 */
public class ReportService {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final ResultService resultService;
    private final CourseService courseService;

    public ReportService() {
        this.studentService = new StudentService();
        this.attendanceService = new AttendanceService();
        this.resultService = new ResultService();
        this.courseService = new CourseService();
    }

    // ===== CSV EXPORTS =====

    public String exportStudentListCSV() throws SQLException, IOException {
        String[] headers = {"Student ID", "First Name", "Last Name", "Email", "Phone",
                           "Gender", "Department", "Year", "Semester", "Status", "Enrollment Date"};
        String[][] data = getStudentData();
        return CSVExporter.export("student_list", headers, data);
    }

    public String exportAttendanceCSV() throws SQLException, IOException {
        String[] headers = {"Student ID", "Student Name", "Course", "Date", "Status"};
        String[][] data = getAttendanceData();
        return CSVExporter.export("attendance_report", headers, data);
    }

    public String exportResultsCSV() throws SQLException, IOException {
        String[] headers = {"Student ID", "Student Name", "Course Code", "Course Name",
                           "Internal", "External", "Total", "Grade", "Semester"};
        String[][] data = getResultsData();
        return CSVExporter.export("academic_results", headers, data);
    }

    public String exportCoursesCSV() throws SQLException, IOException {
        String[] headers = {"Course Code", "Course Name", "Credits", "Department", "Semester"};
        String[][] data = getCourseData();
        return CSVExporter.export("course_list", headers, data);
    }

    // ===== PDF EXPORTS =====

    public String exportStudentListPDF() throws SQLException, IOException {
        String[] headers = {"Student ID", "Name", "Email", "Department", "Year/Sem", "Status"};
        List<Student> students = studentService.getAllStudents();
        String[][] data = new String[students.size()][headers.length];
        
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new String[]{
                s.getStudentId(), s.getFullName(), s.getEmail(),
                s.getDepartmentCode() != null ? s.getDepartmentCode() : "",
                "Y" + s.getYear() + "/S" + s.getSemester(),
                s.getStatus().name()
            };
        }
        return PDFExporter.exportTableReport("Student Master List", "Complete list of registered students", headers, data);
    }

    public String exportAttendancePDF() throws SQLException, IOException {
        String[] headers = {"Student ID", "Student Name", "Course", "Date", "Status"};
        String[][] data = getAttendanceData();
        return PDFExporter.exportTableReport("Attendance Log", "Recent student attendance records", headers, data);
    }

    public String exportResultsPDF() throws SQLException, IOException {
        String[] headers = {"Student ID", "Name", "Course", "Internal", "External", "Total", "Grade"};
        List<Mark> marks = resultService.getAllMarks(1000, 0);
        String[][] data = new String[marks.size()][headers.length];
        
        for (int i = 0; i < marks.size(); i++) {
            Mark m = marks.get(i);
            data[i] = new String[]{
                m.getStudentCode(), m.getStudentName(), m.getCourseCode(),
                String.format("%.0f/40", m.getInternalMarks()), 
                String.format("%.0f/60", m.getExternalMarks()),
                String.format("%.0f/100", m.getTotalMarks()), 
                m.getGrade()
            };
        }
        return PDFExporter.exportTableReport("Academic Results", "Complete semester results", headers, data);
    }

    // ===== HELPER METHODS =====

    private String[][] getStudentData() throws SQLException {
        List<Student> students = studentService.getAllStudents();
        String[][] data = new String[students.size()][11];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new String[]{
                s.getStudentId(), s.getFirstName(), s.getLastName(), s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "",
                s.getGender() != null ? s.getGender().name() : "",
                s.getDepartmentCode() != null ? s.getDepartmentCode() : "",
                String.valueOf(s.getYear()), String.valueOf(s.getSemester()),
                s.getStatus().name(),
                s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : ""
            };
        }
        return data;
    }

    private String[][] getAttendanceData() throws SQLException {
        List<Attendance> records = attendanceService.getAllAttendance(1000, 0);
        String[][] data = new String[records.size()][5];
        for (int i = 0; i < records.size(); i++) {
            Attendance a = records.get(i);
            data[i] = new String[]{
                a.getStudentCode(), a.getStudentName(),
                a.getCourseCode() + " - " + a.getCourseName(),
                a.getAttendanceDate().toString(), a.getStatus().name()
            };
        }
        return data;
    }

    private String[][] getResultsData() throws SQLException {
        List<Mark> marks = resultService.getAllMarks(1000, 0);
        String[][] data = new String[marks.size()][9];
        for (int i = 0; i < marks.size(); i++) {
            Mark m = marks.get(i);
            data[i] = new String[]{
                m.getStudentCode(), m.getStudentName(), m.getCourseCode(), m.getCourseName(),
                String.valueOf(m.getInternalMarks()), String.valueOf(m.getExternalMarks()),
                String.valueOf(m.getTotalMarks()), m.getGrade(), String.valueOf(m.getSemester())
            };
        }
        return data;
    }

    private String[][] getCourseData() throws SQLException {
        List<Course> courses = courseService.getAllCourses();
        String[][] data = new String[courses.size()][5];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            data[i] = new String[]{
                c.getCourseCode(), c.getCourseName(), String.valueOf(c.getCredits()),
                c.getDepartmentCode(), String.valueOf(c.getSemester())
            };
        }
        return data;
    }
}
