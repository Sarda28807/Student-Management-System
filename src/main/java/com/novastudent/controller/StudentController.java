package com.novastudent.controller;

import com.novastudent.model.Department;
import com.novastudent.model.Student;
import com.novastudent.service.CourseService;
import com.novastudent.service.StudentService;
import com.novastudent.service.ReportService;
import com.novastudent.ui.*;
import com.novastudent.util.DateUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Students management page.
 * Handles CRUD, search, filter, and student profiles.
 */
public class StudentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final ReportService reportService;
    private TableView<Student> studentTable;
    private VBox contentContainer;
    private TextField searchField;

    public StudentController() {
        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.reportService = new ReportService();
    }

    /**
     * Loads student list content.
     */
    public void loadContent(VBox container) {
        this.contentContainer = container;
        container.getChildren().clear();

        // Top action bar
        HBox actionBar = new HBox(12);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        searchField = GlassComponents.searchField("Search students...");
        searchField.setPrefWidth(280);
        searchField.setOnAction(e -> performSearch(searchField.getText()));

        // Filters
        ComboBox<String> deptFilter = GlassComponents.glassComboBox("Department");
        deptFilter.setPrefWidth(150);
        deptFilter.getItems().add("All Departments");
        try {
            for (Department d : courseService.getAllDepartments()) {
                deptFilter.getItems().add(d.getDepartmentCode() + " - " + d.getDepartmentName());
            }
        } catch (Exception ignored) {}
        deptFilter.setValue("All Departments");

        ComboBox<String> yearFilter = GlassComponents.glassComboBox("Year");
        yearFilter.setPrefWidth(100);
        yearFilter.getItems().addAll("All Years", "Year 1", "Year 2", "Year 3", "Year 4");
        yearFilter.setValue("All Years");

        ComboBox<String> statusFilter = GlassComponents.glassComboBox("Status");
        statusFilter.setPrefWidth(120);
        statusFilter.getItems().addAll("All", "ACTIVE", "INACTIVE", "GRADUATED", "SUSPENDED");
        statusFilter.setValue("All");

        // Filter action
        Runnable applyFilters = () -> {
            try {
                String dept = deptFilter.getValue();
                Integer deptId = null;
                if (dept != null && !dept.equals("All Departments")) {
                    String code = dept.split(" - ")[0];
                    List<Department> departments = courseService.getAllDepartments();
                    for (Department d : departments) {
                        if (d.getDepartmentCode().equals(code)) { deptId = d.getId(); break; }
                    }
                }
                String yearStr = yearFilter.getValue();
                Integer year = null;
                if (yearStr != null && !yearStr.equals("All Years")) {
                    year = Integer.parseInt(yearStr.replace("Year ", ""));
                }
                String status = statusFilter.getValue();
                if ("All".equals(status)) status = null;

                List<Student> students = studentService.filterStudents(deptId, year, status);
                studentTable.setItems(FXCollections.observableArrayList(students));
            } catch (Exception ex) {
                ToastNotification.error("Filter failed: " + ex.getMessage());
            }
        };

        deptFilter.setOnAction(e -> applyFilters.run());
        yearFilter.setOnAction(e -> applyFilters.run());
        statusFilter.setOnAction(e -> applyFilters.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons
        Button addBtn = GlassComponents.primaryButton("Add Student", IconFactory.ADD);
        addBtn.setOnAction(e -> showStudentForm(null));

        Button exportBtn = GlassComponents.secondaryButton("Export", IconFactory.EXPORT);
        exportBtn.setOnAction(e -> {
            try {
                String path = reportService.exportStudentListCSV();
                ToastNotification.success("Students exported to " + path);
            } catch (Exception ex) {
                ToastNotification.error("Export failed: " + ex.getMessage());
            }
        });

        actionBar.getChildren().addAll(searchField, deptFilter, yearFilter, statusFilter, spacer, addBtn, exportBtn);

        // Table
        studentTable = GlassComponents.glassTable();
        buildTableColumns();
        VBox.setVgrow(studentTable, Priority.ALWAYS);
        studentTable.setPrefHeight(500);

        container.getChildren().addAll(actionBar, studentTable);

        // Load data
        refreshTable();
    }

    /**
     * Performs a search and updates the table.
     */
    public void performSearch(String query) {
        new Thread(() -> {
            try {
                List<Student> results = studentService.searchStudents(query);
                Platform.runLater(() -> {
                    studentTable.setItems(FXCollections.observableArrayList(results));
                    if (searchField != null) searchField.setText(query);
                });
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Search failed: " + e.getMessage()));
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    private void buildTableColumns() {
        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        idCol.setPrefWidth(120);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(180);

        TableColumn<Student, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDepartmentCode() != null ? data.getValue().getDepartmentCode() : "—"));
        deptCol.setPrefWidth(100);

        TableColumn<Student, String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> new SimpleStringProperty("Year " + data.getValue().getYear()));
        yearCol.setPrefWidth(70);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Student, Void> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Student s = getTableRow().getItem();
                    setGraphic(GlassComponents.statusBadge(s.getStatus().name()));
                }
            }
        });

        TableColumn<Student, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(180);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = GlassComponents.smallButton("Edit", null);
            final Button deleteBtn = GlassComponents.smallButton("Delete", "glass-button-danger");
            final Button viewBtn = GlassComponents.smallButton("View", null);
            final HBox box = new HBox(6, viewBtn, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                viewBtn.setOnAction(e -> {
                    Student s = getTableRow().getItem();
                    if (s != null) showStudentProfile(s);
                });
                editBtn.setOnAction(e -> {
                    Student s = getTableRow().getItem();
                    if (s != null) showStudentForm(s);
                });
                deleteBtn.setOnAction(e -> {
                    Student s = getTableRow().getItem();
                    if (s != null) confirmDelete(s);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        studentTable.getColumns().addAll(idCol, nameCol, deptCol, yearCol, emailCol, statusCol, actionsCol);
    }

    private void refreshTable() {
        new Thread(() -> {
            try {
                List<Student> students = studentService.getAllStudents();
                Platform.runLater(() -> studentTable.setItems(FXCollections.observableArrayList(students)));
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Failed to load students: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Shows the add/edit student form.
     */
    private void showStudentForm(Student existing) {
        contentContainer.getChildren().clear();
        boolean isEdit = existing != null;

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = GlassComponents.secondaryButton("Back", IconFactory.CLOSE);
        backBtn.setOnAction(e -> loadContent(contentContainer));
        Label title = new Label(isEdit ? "Edit Student" : "Add New Student");
        title.getStyleClass().add("text-heading");
        header.getChildren().addAll(backBtn, title);

        // Form card
        VBox formCard = GlassComponents.glassPanel();
        formCard.setMaxWidth(800);
        formCard.setSpacing(20);

        // Personal Info
        Label personalTitle = GlassComponents.sectionTitle("Personal Information");

        TextField firstNameField = GlassComponents.glassTextField("First Name");
        TextField lastNameField = GlassComponents.glassTextField("Last Name");
        DatePicker dobField = GlassComponents.glassDatePicker("Date of Birth");
        ComboBox<String> genderField = GlassComponents.glassComboBox("Select Gender");
        genderField.getItems().addAll("MALE", "FEMALE", "OTHER");

        HBox personalRow1 = new HBox(12,
            GlassComponents.formField("First Name *", firstNameField),
            GlassComponents.formField("Last Name *", lastNameField));
        personalRow1.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        HBox personalRow2 = new HBox(12,
            GlassComponents.formField("Date of Birth", dobField),
            GlassComponents.formField("Gender *", genderField));
        personalRow2.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Contact Info
        Label contactTitle = GlassComponents.sectionTitle("Contact Information");

        TextField emailField = GlassComponents.glassTextField("Email Address");
        TextField phoneField = GlassComponents.glassTextField("Phone Number");
        TextArea addressField = GlassComponents.glassTextArea("Full Address", 2);

        HBox contactRow = new HBox(12,
            GlassComponents.formField("Email *", emailField),
            GlassComponents.formField("Phone", phoneField));
        contactRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox addressBox = GlassComponents.formField("Address", addressField);

        // Academic Info
        Label academicTitle = GlassComponents.sectionTitle("Academic Information");

        TextField studentIdField = GlassComponents.glassTextField("e.g., CSE2024001");
        if (isEdit) studentIdField.setDisable(true);

        ComboBox<String> deptField = GlassComponents.glassComboBox("Select Department");
        try {
            for (Department d : courseService.getAllDepartments()) {
                deptField.getItems().add(d.getId() + "|" + d.getDepartmentCode() + " - " + d.getDepartmentName());
            }
        } catch (Exception ignored) {}

        ComboBox<String> yearField = GlassComponents.glassComboBox("Year");
        yearField.getItems().addAll("1", "2", "3", "4");

        ComboBox<String> semesterField = GlassComponents.glassComboBox("Semester");
        semesterField.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8");

        HBox academicRow1 = new HBox(12,
            GlassComponents.formField("Student ID *", studentIdField),
            GlassComponents.formField("Department *", deptField));
        academicRow1.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        HBox academicRow2 = new HBox(12,
            GlassComponents.formField("Year *", yearField),
            GlassComponents.formField("Semester *", semesterField));
        academicRow2.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-wrap-text: true;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Pre-fill if editing
        if (isEdit) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            dobField.setValue(existing.getDateOfBirth());
            genderField.setValue(existing.getGender().name());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhone());
            addressField.setText(existing.getAddress());
            studentIdField.setText(existing.getStudentId());
            yearField.setValue(String.valueOf(existing.getYear()));
            semesterField.setValue(String.valueOf(existing.getSemester()));

            // Select department
            for (String item : deptField.getItems()) {
                if (item.startsWith(existing.getDepartmentId() + "|")) {
                    deptField.setValue(item);
                    break;
                }
            }
        }

        // Buttons
        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = GlassComponents.secondaryButton("Cancel", null);
        cancelBtn.setOnAction(e -> loadContent(contentContainer));

        Button saveBtn = GlassComponents.primaryButton(isEdit ? "Update Student" : "Create Student", IconFactory.SAVE);
        saveBtn.setOnAction(e -> {
            try {
                Student student = isEdit ? existing : new Student();
                student.setFirstName(firstNameField.getText().trim());
                student.setLastName(lastNameField.getText().trim());
                student.setEmail(emailField.getText().trim());
                student.setPhone(phoneField.getText().trim());
                student.setDateOfBirth(dobField.getValue());
                student.setAddress(addressField.getText().trim());

                if (genderField.getValue() != null) {
                    student.setGender(Student.Gender.valueOf(genderField.getValue()));
                }
                if (!isEdit) {
                    student.setStudentId(studentIdField.getText().trim());
                }
                if (deptField.getValue() != null) {
                    student.setDepartmentId(Integer.parseInt(deptField.getValue().split("\\|")[0]));
                }
                if (yearField.getValue() != null) {
                    student.setYear(Integer.parseInt(yearField.getValue()));
                }
                if (semesterField.getValue() != null) {
                    student.setSemester(Integer.parseInt(semesterField.getValue()));
                }
                if (!isEdit) {
                    student.setEnrollmentDate(LocalDate.now());
                }

                List<String> errors;
                if (isEdit) {
                    errors = studentService.updateStudent(student);
                } else {
                    errors = studentService.createStudent(student);
                }

                if (errors.isEmpty()) {
                    ToastNotification.success(isEdit ? "Student updated successfully" : "Student created successfully");
                    loadContent(contentContainer);
                } else {
                    errorLabel.setText(String.join("\n", errors));
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                }
            } catch (Exception ex) {
                ToastNotification.error("Error: " + ex.getMessage());
            }
        });

        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        formCard.getChildren().addAll(
            personalTitle, personalRow1, personalRow2,
            contactTitle, contactRow, addressBox,
            academicTitle, academicRow1, academicRow2,
            errorLabel, btnRow
        );

        contentContainer.getChildren().addAll(header, formCard);
    }

    /**
     * Shows student profile view.
     */
    private void showStudentProfile(Student student) {
        contentContainer.getChildren().clear();

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = GlassComponents.secondaryButton("Back", IconFactory.CLOSE);
        backBtn.setOnAction(e -> loadContent(contentContainer));
        Label title = new Label("Student Profile");
        title.getStyleClass().add("text-heading");
        header.getChildren().addAll(backBtn, title);

        // Profile card
        HBox profileRow = new HBox(20);
        profileRow.setAlignment(Pos.TOP_LEFT);

        // Left: Avatar + basic info
        VBox leftCard = GlassComponents.glassPanel();
        leftCard.setAlignment(Pos.CENTER);
        leftCard.setPrefWidth(300);

        StackPane avatar = GlassComponents.avatar(student.getInitials(), 80);

        Label nameLabel = new Label(student.getFullName());
        nameLabel.getStyleClass().add("text-heading");

        Label idLabel = new Label(student.getStudentId());
        idLabel.setStyle("-fx-text-fill: #8B5CF6; -fx-font-size: 13px; -fx-font-weight: 600;");

        Label deptLabel = new Label(student.getDepartmentName() != null ? student.getDepartmentName() : "—");
        deptLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");

        Label yearLabel = new Label("Year " + student.getYear() + " • Sem " + student.getSemester());
        yearLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        Label statusBadge = GlassComponents.statusBadge(student.getStatus().name());

        leftCard.getChildren().addAll(avatar, nameLabel, idLabel, deptLabel, yearLabel, statusBadge);
        leftCard.setSpacing(6);

        // Right: Details
        VBox rightCard = GlassComponents.glassPanel();
        HBox.setHgrow(rightCard, Priority.ALWAYS);

        Label detailTitle = GlassComponents.sectionTitle("Details");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);

        addDetailRow(grid, 0, "Email", student.getEmail());
        addDetailRow(grid, 1, "Phone", student.getPhone() != null ? student.getPhone() : "—");
        addDetailRow(grid, 2, "Gender", student.getGender() != null ? student.getGender().name() : "—");
        addDetailRow(grid, 3, "Date of Birth", DateUtil.formatDisplay(student.getDateOfBirth()));
        addDetailRow(grid, 4, "Address", student.getAddress() != null ? student.getAddress() : "—");
        addDetailRow(grid, 5, "Enrollment Date", DateUtil.formatDisplay(student.getEnrollmentDate()));

        rightCard.getChildren().addAll(detailTitle, grid);

        profileRow.getChildren().addAll(leftCard, rightCard);

        // Stats row
        HBox statsRow = new HBox(16);
        try {
            com.novastudent.service.AttendanceService attService = new com.novastudent.service.AttendanceService();
            com.novastudent.service.ResultService resService = new com.novastudent.service.ResultService();

            int[] attStats = attService.getStudentAttendanceStats(student.getId());
            double gpa = resService.calculateGPA(student.getId());
            int credits = resService.getTotalCredits(student.getId());
            List<com.novastudent.model.Mark> marks = resService.getStudentMarks(student.getId());

            double attRate = attStats[0] > 0 ? (attStats[1] * 100.0 / attStats[0]) : 0;

            VBox attCard = GlassComponents.statCard(IconFactory.ATTENDANCE, "ATTENDANCE",
                String.format("%.1f%%", attRate), attStats[1] + "/" + attStats[0] + " present", Color.web("#3B82F6"));
            VBox gpaCard = GlassComponents.statCard(IconFactory.GRADE, "CURRENT GPA",
                String.format("%.2f", gpa), com.novastudent.util.GradeCalculator.getClassification(gpa), Color.web("#8B5CF6"));
            VBox courseCard = GlassComponents.statCard(IconFactory.COURSES, "COURSES",
                String.valueOf(marks.size()), "Enrolled courses", Color.web("#22D3EE"));
            VBox creditCard = GlassComponents.statCard(IconFactory.RESULTS, "CREDITS",
                String.valueOf(credits), "Credits earned", Color.web("#22C55E"));

            statsRow.getChildren().addAll(attCard, gpaCard, courseCard, creditCard);
            statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        } catch (Exception ignored) {}

        contentContainer.getChildren().addAll(header, profileRow, statsRow);
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px; -fx-font-weight: 600;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px;");
        v.setWrapText(true);
        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }

    private void confirmDelete(Student student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Delete " + student.getFullName() + "?");
        alert.setContentText("This action cannot be undone. All related attendance and marks data will also be deleted.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (studentService.deleteStudent(student.getId())) {
                        ToastNotification.success("Student deleted successfully");
                        refreshTable();
                    } else {
                        ToastNotification.error("Failed to delete student");
                    }
                } catch (Exception e) {
                    ToastNotification.error("Error: " + e.getMessage());
                }
            }
        });
    }
}
