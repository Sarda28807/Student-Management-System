package com.novastudent.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * Provides reusable validation methods for forms.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile(
        "^[A-Z]{2,5}[0-9]{4,10}$"
    );

    /**
     * Checks if a string is null or empty (after trimming).
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone number format (10-15 digits, optional + prefix).
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim().replaceAll("[\\s-]", "")).matches();
    }

    /**
     * Validates student ID format (e.g., CSE2023001).
     */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId.trim()).matches();
    }

    /**
     * Validates marks are within range.
     */
    public static boolean isValidMarks(double marks, double min, double max) {
        return marks >= min && marks <= max;
    }

    /**
     * Validates year (1-4).
     */
    public static boolean isValidYear(int year) {
        return year >= 1 && year <= 4;
    }

    /**
     * Validates semester (1-8).
     */
    public static boolean isValidSemester(int semester) {
        return semester >= 1 && semester <= 8;
    }

    /**
     * Validates credits (1-6).
     */
    public static boolean isValidCredits(int credits) {
        return credits >= 1 && credits <= 6;
    }

    /**
     * Sanitizes a string for safe display (removes HTML).
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * Safely parses an integer with a default fallback.
     */
    public static int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parses a double with a default fallback.
     */
    public static double parseDoubleSafe(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
