-- ============================================================
-- NovaStudent — Smart Student Management System
-- Database Schema
-- Version 1.0.0
-- ============================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS nova_student_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE nova_student_db;

-- ============================================================
-- USERS TABLE — Authentication & authorization
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    full_name       VARCHAR(100)    NOT NULL,
    role            ENUM('ADMIN', 'STAFF') NOT NULL DEFAULT 'STAFF',
    email           VARCHAR(100),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_users_username (username),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ============================================================
-- DEPARTMENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS departments (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    department_code     VARCHAR(10)     NOT NULL UNIQUE,
    department_name     VARCHAR(100)    NOT NULL,
    hod_name            VARCHAR(100),
    description         TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_dept_code (department_code)
) ENGINE=InnoDB;

-- ============================================================
-- STUDENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    student_id          VARCHAR(20)     NOT NULL UNIQUE,
    first_name          VARCHAR(50)     NOT NULL,
    last_name           VARCHAR(50)     NOT NULL,
    email               VARCHAR(100)    NOT NULL UNIQUE,
    phone               VARCHAR(15),
    gender              ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    date_of_birth       DATE,
    department_id       INT             NOT NULL,
    year                INT             NOT NULL CHECK (year BETWEEN 1 AND 4),
    semester            INT             NOT NULL CHECK (semester BETWEEN 1 AND 8),
    address             TEXT,
    enrollment_date     DATE            NOT NULL,
    status              ENUM('ACTIVE', 'INACTIVE', 'GRADUATED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (department_id) REFERENCES departments(id) ON UPDATE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_student_dept (department_id),
    INDEX idx_student_status (status),
    INDEX idx_student_name (first_name, last_name)
) ENGINE=InnoDB;

-- ============================================================
-- COURSES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    course_code         VARCHAR(15)     NOT NULL UNIQUE,
    course_name         VARCHAR(100)    NOT NULL,
    credits             INT             NOT NULL CHECK (credits BETWEEN 1 AND 6),
    department_id       INT             NOT NULL,
    semester            INT             NOT NULL CHECK (semester BETWEEN 1 AND 8),
    description         TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (department_id) REFERENCES departments(id) ON UPDATE CASCADE,
    INDEX idx_course_code (course_code),
    INDEX idx_course_dept (department_id),
    INDEX idx_course_semester (semester)
) ENGINE=InnoDB;

-- ============================================================
-- ATTENDANCE TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS attendance (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    student_id          INT             NOT NULL,
    course_id           INT             NOT NULL,
    attendance_date     DATE            NOT NULL,
    status              ENUM('PRESENT', 'ABSENT', 'LATE') NOT NULL DEFAULT 'PRESENT',
    remarks             VARCHAR(255),
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_attendance (student_id, course_id, attendance_date),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_attendance_student (student_id),
    INDEX idx_attendance_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- MARKS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS marks (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    student_id          INT             NOT NULL,
    course_id           INT             NOT NULL,
    internal_marks      DECIMAL(5,2)    NOT NULL CHECK (internal_marks BETWEEN 0 AND 40),
    external_marks      DECIMAL(5,2)    NOT NULL CHECK (external_marks BETWEEN 0 AND 60),
    total_marks         DECIMAL(5,2)    GENERATED ALWAYS AS (internal_marks + external_marks) STORED,
    grade               VARCHAR(2),
    semester            INT             NOT NULL,
    academic_year       VARCHAR(10),
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_marks (student_id, course_id, semester),
    INDEX idx_marks_student (student_id),
    INDEX idx_marks_course (course_id),
    INDEX idx_marks_grade (grade)
) ENGINE=InnoDB;

-- ============================================================
-- RESULTS VIEW — Aggregated academic data
-- ============================================================
CREATE OR REPLACE VIEW student_results AS
SELECT
    s.id AS student_db_id,
    s.student_id,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    d.department_name,
    d.department_code,
    s.year,
    s.semester AS current_semester,
    c.course_code,
    c.course_name,
    c.credits,
    m.internal_marks,
    m.external_marks,
    m.total_marks,
    m.grade,
    m.semester AS marks_semester,
    CASE
        WHEN m.grade = 'O'  THEN 10.0
        WHEN m.grade = 'A+' THEN 9.0
        WHEN m.grade = 'A'  THEN 8.0
        WHEN m.grade = 'B+' THEN 7.0
        WHEN m.grade = 'B'  THEN 6.0
        WHEN m.grade = 'C'  THEN 5.0
        WHEN m.grade = 'P'  THEN 4.0
        WHEN m.grade = 'F'  THEN 0.0
        ELSE 0.0
    END AS grade_point
FROM marks m
JOIN students s ON m.student_id = s.id
JOIN courses c ON m.course_id = c.id
JOIN departments d ON s.department_id = d.id;

-- ============================================================
-- TRIGGERS
-- ============================================================

-- Auto-calculate grade when marks are inserted
DELIMITER //
CREATE TRIGGER trg_calculate_grade_insert
BEFORE INSERT ON marks
FOR EACH ROW
BEGIN
    DECLARE total DECIMAL(5,2);
    SET total = NEW.internal_marks + NEW.external_marks;

    IF total >= 90 THEN SET NEW.grade = 'O';
    ELSEIF total >= 80 THEN SET NEW.grade = 'A+';
    ELSEIF total >= 70 THEN SET NEW.grade = 'A';
    ELSEIF total >= 60 THEN SET NEW.grade = 'B+';
    ELSEIF total >= 50 THEN SET NEW.grade = 'B';
    ELSEIF total >= 40 THEN SET NEW.grade = 'C';
    ELSEIF total >= 30 THEN SET NEW.grade = 'P';
    ELSE SET NEW.grade = 'F';
    END IF;
END //

CREATE TRIGGER trg_calculate_grade_update
BEFORE UPDATE ON marks
FOR EACH ROW
BEGIN
    DECLARE total DECIMAL(5,2);
    SET total = NEW.internal_marks + NEW.external_marks;

    IF total >= 90 THEN SET NEW.grade = 'O';
    ELSEIF total >= 80 THEN SET NEW.grade = 'A+';
    ELSEIF total >= 70 THEN SET NEW.grade = 'A';
    ELSEIF total >= 60 THEN SET NEW.grade = 'B+';
    ELSEIF total >= 50 THEN SET NEW.grade = 'B';
    ELSEIF total >= 40 THEN SET NEW.grade = 'C';
    ELSEIF total >= 30 THEN SET NEW.grade = 'P';
    ELSE SET NEW.grade = 'F';
    END IF;
END //
DELIMITER ;
