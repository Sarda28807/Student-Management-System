package com.novastudent.util;

/**
 * Utility for grade calculation and GPA computation.
 * Implements Indian grading system (10-point scale).
 */
public class GradeCalculator {

    /**
     * Calculates grade from total marks (out of 100).
     */
    public static String calculateGrade(double totalMarks) {
        if (totalMarks >= 90) return "O";   // Outstanding
        if (totalMarks >= 80) return "A+";  // Excellent
        if (totalMarks >= 70) return "A";   // Very Good
        if (totalMarks >= 60) return "B+";  // Good
        if (totalMarks >= 50) return "B";   // Above Average
        if (totalMarks >= 40) return "C";   // Average
        if (totalMarks >= 30) return "P";   // Pass
        return "F";                          // Fail
    }

    /**
     * Converts grade to grade point on a 10-point scale.
     */
    public static double gradeToPoint(String grade) {
        if (grade == null) return 0;
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
     * Calculates weighted GPA from grades and credits.
     *
     * @param grades Array of grade strings
     * @param credits Array of course credits (same length as grades)
     * @return Weighted GPA
     */
    public static double calculateGPA(String[] grades, int[] credits) {
        if (grades == null || credits == null || grades.length != credits.length || grades.length == 0) {
            return 0.0;
        }

        double totalGradePoints = 0;
        int totalCredits = 0;

        for (int i = 0; i < grades.length; i++) {
            double gp = gradeToPoint(grades[i]);
            totalGradePoints += gp * credits[i];
            totalCredits += credits[i];
        }

        return totalCredits > 0 ? Math.round(totalGradePoints / totalCredits * 100.0) / 100.0 : 0.0;
    }

    /**
     * Returns a classification string based on GPA.
     */
    public static String getClassification(double gpa) {
        if (gpa >= 9.0) return "Outstanding";
        if (gpa >= 8.0) return "Distinction";
        if (gpa >= 7.0) return "First Class";
        if (gpa >= 6.0) return "Second Class";
        if (gpa >= 5.0) return "Third Class";
        if (gpa >= 4.0) return "Pass";
        return "Fail";
    }

    /**
     * Returns a CSS style class for grade badge coloring.
     */
    public static String getGradeStyleClass(String grade) {
        if (grade == null) return "grade-default";
        switch (grade) {
            case "O":
            case "A+": return "grade-excellent";
            case "A":
            case "B+": return "grade-good";
            case "B":
            case "C":  return "grade-average";
            case "P":  return "grade-pass";
            case "F":  return "grade-fail";
            default:   return "grade-default";
        }
    }
}
