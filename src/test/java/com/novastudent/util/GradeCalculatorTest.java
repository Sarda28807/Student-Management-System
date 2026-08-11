package com.novastudent.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GradeCalculatorTest {

    @Test
    void testCalculateGrade() {
        assertEquals("O", GradeCalculator.calculateGrade(95.0));
        assertEquals("A+", GradeCalculator.calculateGrade(85.0));
        assertEquals("A", GradeCalculator.calculateGrade(75.0));
        assertEquals("B+", GradeCalculator.calculateGrade(65.0));
        assertEquals("B", GradeCalculator.calculateGrade(55.0));
        assertEquals("C", GradeCalculator.calculateGrade(45.0));
        assertEquals("P", GradeCalculator.calculateGrade(35.0));
        assertEquals("F", GradeCalculator.calculateGrade(25.0));
    }

    @Test
    void testGradeToPoint() {
        assertEquals(10.0, GradeCalculator.gradeToPoint("O"));
        assertEquals(9.0, GradeCalculator.gradeToPoint("A+"));
        assertEquals(0.0, GradeCalculator.gradeToPoint("F"));
        assertEquals(0.0, GradeCalculator.gradeToPoint("Invalid"));
        assertEquals(0.0, GradeCalculator.gradeToPoint(null));
    }

    @Test
    void testCalculateGPA() {
        String[] grades = {"O", "A", "B+"};
        int[] credits = {4, 3, 3};
        
        // (10*4 + 8*3 + 7*3) / 10 = (40 + 24 + 21) / 10 = 85 / 10 = 8.5
        assertEquals(8.5, GradeCalculator.calculateGPA(grades, credits));

        // Edge cases
        assertEquals(0.0, GradeCalculator.calculateGPA(null, null));
        assertEquals(0.0, GradeCalculator.calculateGPA(new String[0], new int[0]));
        assertEquals(0.0, GradeCalculator.calculateGPA(new String[]{"A"}, new int[]{2, 3})); // Mismatched lengths
    }

    @Test
    void testGetClassification() {
        assertEquals("Outstanding", GradeCalculator.getClassification(9.5));
        assertEquals("First Class", GradeCalculator.getClassification(7.5));
        assertEquals("Fail", GradeCalculator.getClassification(3.0));
    }
}
