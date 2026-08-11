package com.novastudent.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void testIsEmpty() {
        assertTrue(ValidationUtil.isEmpty(null));
        assertTrue(ValidationUtil.isEmpty(""));
        assertTrue(ValidationUtil.isEmpty("   "));
        assertFalse(ValidationUtil.isEmpty("text"));
    }

    @Test
    void testIsValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name+tag@student.edu"));
        
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("test@.com"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testIsValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertTrue(ValidationUtil.isValidPhone("+123456789012"));
        assertTrue(ValidationUtil.isValidPhone("123-456-7890")); // Method cleans dashes/spaces
        
        assertFalse(ValidationUtil.isValidPhone("123")); // Too short
        assertFalse(ValidationUtil.isValidPhone("abcdefghij"));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    void testIsValidStudentId() {
        assertTrue(ValidationUtil.isValidStudentId("CSE2024001"));
        assertTrue(ValidationUtil.isValidStudentId("IT1234"));
        
        assertFalse(ValidationUtil.isValidStudentId("C123")); // Too short letters
        assertFalse(ValidationUtil.isValidStudentId("CSE12")); // Too short numbers
        assertFalse(ValidationUtil.isValidStudentId("cse2024001")); // Lowercase not allowed by regex
        assertFalse(ValidationUtil.isValidStudentId(null));
    }

    @Test
    void testParseIntSafe() {
        assertEquals(5, ValidationUtil.parseIntSafe("5", 0));
        assertEquals(0, ValidationUtil.parseIntSafe("invalid", 0));
        assertEquals(10, ValidationUtil.parseIntSafe(null, 10));
    }
}
