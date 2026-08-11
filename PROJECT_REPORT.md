# Project Report: NovaStudent Management System

## 1. Executive Summary
NovaStudent is a comprehensive, desktop-based Student Management System developed using Java, JavaFX, and MySQL. It aims to solve the administrative bottleneck of managing student lifecycles, attendance, and academic results in modern educational institutions. The system moves away from traditional, outdated UI designs by implementing a custom "glassmorphism" aesthetic, proving that enterprise software can be both highly functional and visually engaging.

## 2. System Architecture

The application was designed using a strict **Layered MVC (Model-View-Controller)** architectural pattern to ensure high cohesion, low coupling, and testability.

### 2.1 The Layers
1.  **Presentation (UI/Controller) Layer:**
    *   Unlike traditional JavaFX apps that rely on `.fxml` files and SceneBuilder, NovaStudent generates its UI programmatically via the `GlassComponents` factory. This provides maximum control over complex CSS styling, animations, and dynamic component rendering.
    *   Controllers (e.g., `StudentController`, `DashboardController`) handle user input, update the views, and delegate business logic to the Service Layer.
2.  **Service Layer:**
    *   Acts as the central nervous system (e.g., `StudentService`, `ReportService`).
    *   Enforces business rules and validation *before* data reaches the database. For example, `StudentService.validateStudent()` ensures email uniqueness and proper ID formats.
3.  **Data Access Object (DAO) Layer:**
    *   Abstracts the database interaction (e.g., `StudentDAO`).
    *   Uses pure JDBC `PreparedStatement` to prevent SQL injection attacks.
    *   Maps relational database rows into Java Objects (`ResultSet` to `Model`).
4.  **Database Layer:**
    *   MySQL 8.0 backend utilizing advanced constraints, triggers, and generated columns to push data integrity checks down to the data tier.

## 3. Database Design

The database (`novastudent_db`) is normalized to the 3rd Normal Form (3NF).

### 3.1 Core Entities
*   **users:** System administrators (with BCrypt hashed passwords).
*   **departments:** Academic branches.
*   **courses:** Subjects offered, linked to departments.
*   **students:** Core profiles, utilizing ENUMs for Status and Gender.
*   **attendance:** Tracks daily presence, enforcing a unique constraint on `(student_id, course_id, attendance_date)`.
*   **marks:** Academic results.

### 3.2 Advanced Database Features Utilized
*   **Generated Columns:** `total_marks` in the `marks` table is defined as `(internal_marks + external_marks)`.
*   **Triggers:** `BEFORE INSERT` and `BEFORE UPDATE` triggers automatically calculate the letter grade (O, A+, A, B, etc.) based on the total marks, ensuring the database is the single source of truth for grading logic.
*   **Cascading Deletes:** Deleting a student automatically cleans up their attendance and marks records via `ON DELETE CASCADE` foreign keys.

## 4. Key Technical Decisions

### 4.1 Programmatic UI over FXML
FXML was deliberately avoided to implement the complex CSS required for glassmorphism (translucency, background blurs, drop shadows). A central `GlassComponents` factory was created to ensure design consistency (DRY principle) across all screens.

### 4.2 Security Implementation
Passwords are never stored in plain text. The application uses `jBCrypt` to hash passwords with a work factor of 10. The `AuthenticationService` and `SessionManager` work in tandem to secure the application state.

### 4.3 Async Operations
Database calls (especially for dashboard analytics and search) are wrapped in background Threads, with the results returned to the UI thread via `Platform.runLater()`. This ensures the UI remains smooth and responsive during heavy queries.

## 5. Future Enhancements
*   **Role-Based Access Control (RBAC):** Expanding the user system to allow Student and Teacher logins with restricted views.
*   **Cloud Database Integration:** Migrating from a local MySQL instance to AWS RDS.
*   **Email Notifications:** Integrating JavaMail API to send automated attendance alerts to students.

## 6. Conclusion
NovaStudent successfully demonstrates the ability to architect, design, and implement a full-stack Java application from scratch. It showcases practical mastery of OOP, JDBC, database design, and modern UI/UX principles.
