-- ============================================================
-- NovaStudent — Sample Data
-- Version 1.0.0
-- ============================================================
-- WARNING: This is sample/demo data for testing purposes only.
-- Do NOT load this into a production database.
-- ============================================================

USE nova_student_db;

-- ============================================================
-- DEFAULT ADMIN USER
-- Password: Admin@123 (BCrypt hashed)
-- IMPORTANT: Change this password immediately after first login
-- ============================================================
INSERT INTO users (username, password_hash, full_name, role, email) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'ADMIN', 'admin@novastudent.edu'),
('staff1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rajesh Kumar', 'STAFF', 'rajesh@novastudent.edu');

-- ============================================================
-- DEPARTMENTS
-- ============================================================
INSERT INTO departments (department_code, department_name, hod_name, description) VALUES
('CSE', 'Computer Science and Engineering', 'Dr. Ramesh Krishnan', 'Department of Computer Science covering software engineering, AI, data science, and systems.'),
('ECE', 'Electronics and Communication Engineering', 'Dr. Sunita Verma', 'Department of Electronics covering VLSI, embedded systems, and communications.'),
('EEE', 'Electrical and Electronics Engineering', 'Dr. Anil Mehta', 'Department of Electrical Engineering covering power systems and control engineering.'),
('ME', 'Mechanical Engineering', 'Dr. Praveen Nair', 'Department of Mechanical Engineering covering thermodynamics, manufacturing, and design.'),
('CIVIL', 'Civil Engineering', 'Dr. Lakshmi Iyer', 'Department of Civil Engineering covering structural, environmental, and transportation engineering.');

-- ============================================================
-- COURSES
-- ============================================================
INSERT INTO courses (course_code, course_name, credits, department_id, semester, description) VALUES
-- CSE Courses
('CS101', 'Programming Fundamentals', 4, 1, 1, 'Introduction to programming with C and problem solving.'),
('CS201', 'Data Structures', 4, 1, 3, 'Linear and non-linear data structures with algorithms.'),
('CS301', 'Database Management Systems', 4, 1, 5, 'Relational databases, SQL, normalization, and transaction management.'),
('CS302', 'Operating Systems', 4, 1, 5, 'Process management, memory management, file systems.'),
('CS401', 'Machine Learning', 3, 1, 7, 'Supervised and unsupervised learning algorithms.'),
('CS402', 'Software Engineering', 3, 1, 7, 'Software development lifecycle, agile, and design patterns.'),

-- ECE Courses
('EC101', 'Basic Electronics', 4, 2, 1, 'Fundamentals of electronic devices and circuits.'),
('EC201', 'Signals and Systems', 4, 2, 3, 'Continuous and discrete-time signal processing.'),
('EC301', 'Digital Communication', 3, 2, 5, 'Modulation techniques and communication systems.'),

-- EEE Courses
('EE101', 'Circuit Theory', 4, 3, 1, 'Fundamentals of electrical circuit analysis.'),
('EE201', 'Electrical Machines', 4, 3, 3, 'DC and AC machines, transformers.'),
('EE301', 'Power Systems', 3, 3, 5, 'Generation, transmission, and distribution of power.'),

-- ME Courses
('ME101', 'Engineering Mechanics', 4, 4, 1, 'Statics, dynamics, and mechanics of materials.'),
('ME201', 'Thermodynamics', 4, 4, 3, 'Laws of thermodynamics and heat transfer.'),
('ME301', 'Manufacturing Processes', 3, 4, 5, 'Casting, welding, machining, and forming.'),

-- CIVIL Courses
('CE101', 'Engineering Drawing', 3, 5, 1, 'Technical drawing and CAD fundamentals.'),
('CE201', 'Strength of Materials', 4, 5, 3, 'Stress, strain, and structural analysis.'),
('CE301', 'Structural Analysis', 4, 5, 5, 'Analysis of determinate and indeterminate structures.');

-- ============================================================
-- STUDENTS
-- ============================================================
INSERT INTO students (student_id, first_name, last_name, email, phone, gender, date_of_birth, department_id, year, semester, address, enrollment_date, status) VALUES
-- CSE Students
('CSE2023001', 'Rahul', 'Sharma', 'rahul.sharma@university.edu', '9876543210', 'MALE', '2004-03-15', 1, 3, 5, '12, MG Road, Bangalore', '2023-08-01', 'ACTIVE'),
('CSE2023002', 'Ananya', 'Patel', 'ananya.patel@university.edu', '9876543211', 'FEMALE', '2004-07-22', 1, 3, 5, '45, Park Street, Mumbai', '2023-08-01', 'ACTIVE'),
('CSE2023003', 'Arjun', 'Singh', 'arjun.singh@university.edu', '9876543212', 'MALE', '2004-01-10', 1, 3, 5, '78, Nehru Place, Delhi', '2023-08-01', 'ACTIVE'),
('CSE2023004', 'Priya', 'Menon', 'priya.menon@university.edu', '9876543213', 'FEMALE', '2004-11-05', 1, 3, 5, '23, Anna Nagar, Chennai', '2023-08-01', 'ACTIVE'),
('CSE2024001', 'Vikram', 'Reddy', 'vikram.reddy@university.edu', '9876543214', 'MALE', '2005-05-20', 1, 2, 3, '56, Jubilee Hills, Hyderabad', '2024-08-01', 'ACTIVE'),
('CSE2024002', 'Sneha', 'Gupta', 'sneha.gupta@university.edu', '9876543215', 'FEMALE', '2005-09-12', 1, 2, 3, '89, Sector 15, Noida', '2024-08-01', 'ACTIVE'),

-- ECE Students
('ECE2023001', 'Karthik', 'Nair', 'karthik.nair@university.edu', '9876543216', 'MALE', '2004-04-18', 2, 3, 5, '34, Marine Drive, Kochi', '2023-08-01', 'ACTIVE'),
('ECE2023002', 'Divya', 'Krishnan', 'divya.krishnan@university.edu', '9876543217', 'FEMALE', '2004-12-30', 2, 3, 5, '67, Residency Road, Trivandrum', '2023-08-01', 'ACTIVE'),
('ECE2024001', 'Aditya', 'Joshi', 'aditya.joshi@university.edu', '9876543218', 'MALE', '2005-02-14', 2, 2, 3, '90, FC Road, Pune', '2024-08-01', 'ACTIVE'),

-- EEE Students
('EEE2023001', 'Meera', 'Sundaram', 'meera.sundaram@university.edu', '9876543219', 'FEMALE', '2004-08-25', 3, 3, 5, '12, T Nagar, Chennai', '2023-08-01', 'ACTIVE'),
('EEE2024001', 'Rohan', 'Desai', 'rohan.desai@university.edu', '9876543220', 'MALE', '2005-06-08', 3, 2, 3, '45, Koregaon Park, Pune', '2024-08-01', 'ACTIVE'),

-- ME Students
('ME2023001', 'Suresh', 'Babu', 'suresh.babu@university.edu', '9876543221', 'MALE', '2004-10-03', 4, 3, 5, '78, Sadar Bazar, Jaipur', '2023-08-01', 'ACTIVE'),
('ME2023002', 'Lakshmi', 'Rao', 'lakshmi.rao@university.edu', '9876543222', 'FEMALE', '2004-02-28', 4, 3, 5, '23, Banjara Hills, Hyderabad', '2023-08-01', 'ACTIVE'),
('ME2024001', 'Amit', 'Tiwari', 'amit.tiwari@university.edu', '9876543223', 'MALE', '2005-07-16', 4, 2, 3, '56, Civil Lines, Lucknow', '2024-08-01', 'ACTIVE'),

-- CIVIL Students
('CE2023001', 'Deepa', 'Mishra', 'deepa.mishra@university.edu', '9876543224', 'FEMALE', '2004-09-11', 5, 3, 5, '89, Connaught Place, Delhi', '2023-08-01', 'ACTIVE'),
('CE2024001', 'Nikhil', 'Pandey', 'nikhil.pandey@university.edu', '9876543225', 'MALE', '2005-04-05', 5, 2, 3, '34, Gomti Nagar, Lucknow', '2024-08-01', 'ACTIVE'),

-- Graduated / Inactive
('CSE2022001', 'Pooja', 'Agarwal', 'pooja.agarwal@university.edu', '9876543226', 'FEMALE', '2003-06-14', 1, 4, 8, '67, Lajpat Nagar, Delhi', '2022-08-01', 'GRADUATED'),
('ECE2022001', 'Sanjay', 'Kapoor', 'sanjay.kapoor@university.edu', '9876543227', 'MALE', '2003-11-22', 2, 4, 8, '90, Boat Club Road, Pune', '2022-08-01', 'INACTIVE');

-- ============================================================
-- ATTENDANCE (Sample — last 7 days for active students)
-- ============================================================
INSERT INTO attendance (student_id, course_id, attendance_date, status) VALUES
-- Rahul Sharma (id=1) - CS301
(1, 3, CURDATE(), 'PRESENT'),
(1, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),
(1, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'PRESENT'),
(1, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'LATE'),
(1, 3, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'PRESENT'),
(1, 4, CURDATE(), 'PRESENT'),
(1, 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ABSENT'),

-- Ananya Patel (id=2) - CS301
(2, 3, CURDATE(), 'PRESENT'),
(2, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),
(2, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'PRESENT'),
(2, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'PRESENT'),
(2, 4, CURDATE(), 'PRESENT'),
(2, 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),

-- Arjun Singh (id=3) - CS301
(3, 3, CURDATE(), 'PRESENT'),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ABSENT'),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'PRESENT'),
(3, 4, CURDATE(), 'LATE'),

-- Priya Menon (id=4) - CS301
(4, 3, CURDATE(), 'ABSENT'),
(4, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),
(4, 4, CURDATE(), 'PRESENT'),

-- Karthik Nair (id=7) - EC301
(7, 9, CURDATE(), 'PRESENT'),
(7, 9, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),
(7, 9, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'LATE'),

-- Divya Krishnan (id=8) - EC301
(8, 9, CURDATE(), 'PRESENT'),
(8, 9, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ABSENT'),

-- Vikram Reddy (id=5) - CS201
(5, 2, CURDATE(), 'PRESENT'),
(5, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),

-- Sneha Gupta (id=6) - CS201
(6, 2, CURDATE(), 'PRESENT'),
(6, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'LATE'),

-- Meera Sundaram (id=10) - EE301
(10, 12, CURDATE(), 'PRESENT'),
(10, 12, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),

-- Suresh Babu (id=12) - ME301
(12, 15, CURDATE(), 'ABSENT'),
(12, 15, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT'),

-- Deepa Mishra (id=15) - CE301
(15, 18, CURDATE(), 'PRESENT'),
(15, 18, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'PRESENT');

-- ============================================================
-- MARKS (Sample academic records)
-- ============================================================
INSERT INTO marks (student_id, course_id, internal_marks, external_marks, semester, academic_year) VALUES
-- Rahul Sharma — top performer
(1, 1, 38, 55, 1, '2023-24'),
(1, 2, 36, 52, 3, '2024-25'),
(1, 3, 37, 54, 5, '2025-26'),

-- Ananya Patel — top performer
(2, 1, 39, 56, 1, '2023-24'),
(2, 2, 37, 53, 3, '2024-25'),
(2, 3, 38, 55, 5, '2025-26'),

-- Arjun Singh — strong performer
(3, 1, 35, 50, 1, '2023-24'),
(3, 2, 34, 48, 3, '2024-25'),
(3, 3, 36, 52, 5, '2025-26'),

-- Priya Menon — good performer
(4, 1, 32, 45, 1, '2023-24'),
(4, 2, 30, 42, 3, '2024-25'),
(4, 3, 33, 47, 5, '2025-26'),

-- Vikram Reddy
(5, 1, 36, 48, 1, '2024-25'),
(5, 2, 34, 46, 3, '2025-26'),

-- Sneha Gupta
(6, 1, 38, 52, 1, '2024-25'),
(6, 2, 35, 49, 3, '2025-26'),

-- Karthik Nair — ECE
(7, 7, 33, 48, 1, '2023-24'),
(7, 8, 35, 46, 3, '2024-25'),
(7, 9, 34, 50, 5, '2025-26'),

-- Divya Krishnan — ECE
(8, 7, 36, 52, 1, '2023-24'),
(8, 8, 37, 50, 3, '2024-25'),

-- Meera Sundaram — EEE
(10, 10, 34, 48, 1, '2023-24'),
(10, 11, 32, 45, 3, '2024-25'),
(10, 12, 35, 49, 5, '2025-26'),

-- Suresh Babu — ME
(12, 13, 30, 42, 1, '2023-24'),
(12, 14, 28, 38, 3, '2024-25'),

-- Lakshmi Rao — ME
(13, 13, 35, 50, 1, '2023-24'),
(13, 14, 33, 47, 3, '2024-25'),

-- Deepa Mishra — CIVIL
(15, 16, 32, 44, 1, '2023-24'),
(15, 17, 34, 48, 3, '2024-25'),
(15, 18, 36, 50, 5, '2025-26'),

-- Graduated student marks
(17, 1, 37, 53, 1, '2022-23'),
(17, 2, 38, 55, 3, '2023-24');
