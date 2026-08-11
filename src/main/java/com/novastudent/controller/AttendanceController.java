package com.novastudent.controller;

import com.novastudent.model.*;
import com.novastudent.model.Attendance.AttendanceStatus;
import com.novastudent.service.*;
import com.novastudent.ui.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for attendance management.
 * Supports viewing, marking, bulk marking, and filtering attendance.
 */
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final ReportService reportService;
    private TableView<Attendance> attendanceTable;
    private VBox contentContainer;

    public AttendanceController() {
        this.attendanceService = new AttendanceService();
        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.reportService = new ReportService();
    }

    @SuppressWarnings("unchecked")
    public void loadContent(VBox container) {
        this.contentContainer = container;
        container.getChildren().clear();

        // Filter bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        DatePicker dateFilter = GlassComponents.glassDatePicker("Select Date");
        dateFilter.setValue(LocalDate.now());
        dateFilter.setPrefWidth(160);

        ComboBox<String> deptFilter = GlassComponents.glassComboBox("Department");
        deptFilter.setPrefWidth(150);
        deptFilter.getItems().add("All");
        try {
            for (Department d : courseService.getAllDepartments()) {
                deptFilter.getItems().add(d.getDepartmentCode());
            }
        } catch (Exception ignored) {}
        deptFilter.setValue("All");

        ComboBox<String> statusFilter = GlassComponents.glassComboBox("Status");
        statusFilter.setPrefWidth(120);
        statusFilter.getItems().addAll("All", "PRESENT", "ABSENT", "LATE");
        statusFilter.setValue("All");

        Button filterBtn = GlassComponents.secondaryButton("Apply Filter", IconFactory.FILTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button markBtn = GlassComponents.primaryButton("Mark Attendance", IconFactory.ADD);
        markBtn.setOnAction(e -> showMarkAttendanceForm());

        Button exportBtn = GlassComponents.secondaryButton("Export", IconFactory.EXPORT);
        exportBtn.setOnAction(e -> {
            try {
                String path = reportService.exportAttendanceCSV();
                ToastNotification.success("Attendance exported to " + path);
            } catch (Exception ex) {
                ToastNotification.error("Export failed: " + ex.getMessage());
            }
        });

        filterBar.getChildren().addAll(dateFilter, deptFilter, statusFilter, filterBtn, spacer, markBtn, exportBtn);

        // Filter action
        filterBtn.setOnAction(e -> {
            try {
                LocalDate date = dateFilter.getValue();
                String dept = deptFilter.getValue();
                Integer deptId = null;
                if (dept != null && !dept.equals("All")) {
                    for (Department d : courseService.getAllDepartments()) {
                        if (d.getDepartmentCode().equals(dept)) { deptId = d.getId(); break; }
                    }
                }
                String status = statusFilter.getValue();
                if ("All".equals(status)) status = null;

                List<Attendance> records = attendanceService.filterAttendance(date, deptId, null, status);
                attendanceTable.setItems(FXCollections.observableArrayList(records));
            } catch (Exception ex) {
                ToastNotification.error("Filter failed: " + ex.getMessage());
            }
        });

        // Table
        attendanceTable = GlassComponents.glassTable();
        attendanceTable.setPrefHeight(500);

        TableColumn<Attendance, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getStudentCode() + " - " + data.getValue().getStudentName()));
        studentCol.setPrefWidth(250);

        TableColumn<Attendance, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCourseCode() + " - " + data.getValue().getCourseName()));
        courseCol.setPrefWidth(250);

        TableColumn<Attendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getAttendanceDate().toString()));
        dateCol.setPrefWidth(120);

        TableColumn<Attendance, Void> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(GlassComponents.statusBadge(getTableRow().getItem().getStatus().name()));
                }
            }
        });

        TableColumn<Attendance, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(80);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            final Button deleteBtn = GlassComponents.smallButton("Delete", "glass-button-danger");
            {
                deleteBtn.setOnAction(e -> {
                    Attendance a = getTableRow().getItem();
                    if (a != null) {
                        try {
                            attendanceService.deleteAttendance(a.getId());
                            ToastNotification.success("Attendance record deleted");
                            loadContent(contentContainer);
                        } catch (Exception ex) {
                            ToastNotification.error("Error: " + ex.getMessage());
                        }
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        attendanceTable.getColumns().addAll(studentCol, courseCol, dateCol, statusCol, actionsCol);

        container.getChildren().addAll(filterBar, attendanceTable);
        VBox.setVgrow(attendanceTable, Priority.ALWAYS);

        // Load data
        refreshTable();
    }

    private void showMarkAttendanceForm() {
        contentContainer.getChildren().clear();

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = GlassComponents.secondaryButton("Back", IconFactory.CLOSE);
        backBtn.setOnAction(e -> loadContent(contentContainer));
        Label title = new Label("Mark Attendance");
        title.getStyleClass().add("text-heading");
        header.getChildren().addAll(backBtn, title);

        VBox form = GlassComponents.glassPanel();
        form.setMaxWidth(600);
        form.setSpacing(16);

        // Course selector
        ComboBox<String> courseField = GlassComponents.glassComboBox("Select Course");
        try {
            for (Course c : courseService.getAllCourses()) {
                courseField.getItems().add(c.getId() + "|" + c.getCourseCode() + " - " + c.getCourseName());
            }
        } catch (Exception ignored) {}

        DatePicker dateField = GlassComponents.glassDatePicker("Attendance Date");
        dateField.setValue(LocalDate.now());

        // Student list with status selection
        VBox studentListBox = new VBox(8);
        Label listLabel = new Label("Students");
        listLabel.getStyleClass().add("form-label");

        ScrollPane scrollPane = new ScrollPane(studentListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        List<HBox> studentRows = new ArrayList<>();
        List<ComboBox<String>> statusCombos = new ArrayList<>();
        List<Integer> studentIds = new ArrayList<>();

        // Load students when course selected
        courseField.setOnAction(e -> {
            studentListBox.getChildren().clear();
            studentRows.clear();
            statusCombos.clear();
            studentIds.clear();

            if (courseField.getValue() == null) return;

            int courseId = Integer.parseInt(courseField.getValue().split("\\|")[0]);
            try {
                // Get students in the department of this course
                Course selectedCourse = courseService.getCourseById(courseId);
                List<Student> students = studentService.filterStudents(selectedCourse.getDepartmentId(), null, "ACTIVE");

                studentListBox.getChildren().add(listLabel);
                for (Student s : students) {
                    HBox row = new HBox(12);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(6, 10, 6, 10));
                    row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 8;");

                    Label nameLabel = new Label(s.getStudentId() + " - " + s.getFullName());
                    nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px;");
                    HBox.setHgrow(nameLabel, Priority.ALWAYS);

                    ComboBox<String> statusCombo = GlassComponents.glassComboBox("Status");
                    statusCombo.getItems().addAll("PRESENT", "ABSENT", "LATE");
                    statusCombo.setValue("PRESENT");
                    statusCombo.setPrefWidth(120);

                    row.getChildren().addAll(nameLabel, statusCombo);
                    studentListBox.getChildren().add(row);

                    studentRows.add(row);
                    statusCombos.add(statusCombo);
                    studentIds.add(s.getId());
                }
            } catch (Exception ex) {
                ToastNotification.error("Failed to load students");
            }
        });

        // Save button
        Button saveBtn = GlassComponents.primaryButton("Save Attendance", IconFactory.SAVE);
        saveBtn.setOnAction(e -> {
            if (courseField.getValue() == null || dateField.getValue() == null) {
                ToastNotification.warning("Please select course and date");
                return;
            }
            try {
                int courseId = Integer.parseInt(courseField.getValue().split("\\|")[0]);
                List<Attendance> records = new ArrayList<>();
                for (int i = 0; i < studentIds.size(); i++) {
                    Attendance a = new Attendance();
                    a.setStudentId(studentIds.get(i));
                    a.setCourseId(courseId);
                    a.setAttendanceDate(dateField.getValue());
                    a.setStatus(AttendanceStatus.valueOf(statusCombos.get(i).getValue()));
                    records.add(a);
                }
                int count = attendanceService.bulkMarkAttendance(records);
                ToastNotification.success("Attendance marked for " + count + " students");
                loadContent(contentContainer);
            } catch (Exception ex) {
                ToastNotification.error("Error: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
            GlassComponents.formField("Course *", courseField),
            GlassComponents.formField("Date *", dateField),
            scrollPane,
            saveBtn
        );

        contentContainer.getChildren().addAll(header, form);
    }

    private void refreshTable() {
        new Thread(() -> {
            try {
                List<Attendance> records = attendanceService.getAllAttendance(200, 0);
                Platform.runLater(() -> attendanceTable.setItems(FXCollections.observableArrayList(records)));
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Failed to load attendance"));
            }
        }).start();
    }
}
