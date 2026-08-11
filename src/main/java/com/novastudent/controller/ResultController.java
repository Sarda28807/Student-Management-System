package com.novastudent.controller;

import com.novastudent.model.*;
import com.novastudent.service.*;
import com.novastudent.ui.*;
import com.novastudent.util.GradeCalculator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Controller for academic results management.
 */
public class ResultController {

    private final ResultService resultService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final ReportService reportService;
    private TableView<Mark> resultTable;
    private VBox contentContainer;

    public ResultController() {
        this.resultService = new ResultService();
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

        TextField searchField = GlassComponents.searchField("Search by student...");
        searchField.setPrefWidth(220);

        ComboBox<String> semFilter = GlassComponents.glassComboBox("Semester");
        semFilter.setPrefWidth(120);
        semFilter.getItems().add("All");
        for (int i = 1; i <= 8; i++) semFilter.getItems().add("Sem " + i);
        semFilter.setValue("All");

        ComboBox<String> deptFilter = GlassComponents.glassComboBox("Department");
        deptFilter.setPrefWidth(150);
        deptFilter.getItems().add("All");
        try {
            for (Department d : courseService.getAllDepartments()) {
                deptFilter.getItems().add(d.getId() + "|" + d.getDepartmentCode());
            }
        } catch (Exception ignored) {}
        deptFilter.setValue("All");

        Button filterBtn = GlassComponents.secondaryButton("Apply", IconFactory.FILTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = GlassComponents.primaryButton("Enter Marks", IconFactory.ADD);
        addBtn.setOnAction(e -> showMarksForm());

        Button exportBtn = GlassComponents.secondaryButton("Export", IconFactory.EXPORT);
        exportBtn.setOnAction(e -> {
            try {
                String path = reportService.exportResultsCSV();
                ToastNotification.success("Results exported to " + path);
            } catch (Exception ex) {
                ToastNotification.error("Export failed: " + ex.getMessage());
            }
        });

        filterBar.getChildren().addAll(searchField, semFilter, deptFilter, filterBtn, spacer, addBtn, exportBtn);

        // Apply filter
        filterBtn.setOnAction(e -> {
            try {
                String semStr = semFilter.getValue();
                Integer sem = null;
                if (semStr != null && !semStr.equals("All")) sem = Integer.parseInt(semStr.replace("Sem ", ""));

                String deptStr = deptFilter.getValue();
                Integer deptId = null;
                if (deptStr != null && !deptStr.equals("All")) deptId = Integer.parseInt(deptStr.split("\\|")[0]);

                resultTable.setItems(FXCollections.observableArrayList(resultService.filterMarks(sem, deptId, null)));
            } catch (Exception ex) {
                ToastNotification.error("Filter failed: " + ex.getMessage());
            }
        });

        // Table
        resultTable = GlassComponents.glassTable();
        resultTable.setPrefHeight(500);

        TableColumn<Mark, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getStudentCode() + " - " + data.getValue().getStudentName()));
        studentCol.setPrefWidth(220);

        TableColumn<Mark, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourseCode()));
        courseCol.setPrefWidth(100);

        TableColumn<Mark, String> internalCol = new TableColumn<>("Internal");
        internalCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%.0f/40", data.getValue().getInternalMarks())));
        internalCol.setPrefWidth(80);

        TableColumn<Mark, String> externalCol = new TableColumn<>("External");
        externalCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%.0f/60", data.getValue().getExternalMarks())));
        externalCol.setPrefWidth(80);

        TableColumn<Mark, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%.0f/100", data.getValue().getTotalMarks())));
        totalCol.setPrefWidth(80);

        TableColumn<Mark, Void> gradeCol = new TableColumn<>("Grade");
        gradeCol.setPrefWidth(80);
        gradeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(GlassComponents.gradeBadge(getTableRow().getItem().getGrade()));
                }
            }
        });

        TableColumn<Mark, String> gpaCol = new TableColumn<>("GPA");
        gpaCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%.1f", data.getValue().getGradePoint())));
        gpaCol.setPrefWidth(60);

        TableColumn<Mark, String> semCol2 = new TableColumn<>("Sem");
        semCol2.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSemester())));
        semCol2.setPrefWidth(50);

        resultTable.getColumns().addAll(studentCol, courseCol, internalCol, externalCol, totalCol, gradeCol, gpaCol, semCol2);

        container.getChildren().addAll(filterBar, resultTable);
        VBox.setVgrow(resultTable, Priority.ALWAYS);

        refreshTable();
    }

    private void showMarksForm() {
        contentContainer.getChildren().clear();

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = GlassComponents.secondaryButton("Back", IconFactory.CLOSE);
        backBtn.setOnAction(e -> loadContent(contentContainer));
        Label title = new Label("Enter Marks");
        title.getStyleClass().add("text-heading");
        header.getChildren().addAll(backBtn, title);

        VBox form = GlassComponents.glassPanel();
        form.setMaxWidth(600);
        form.setSpacing(16);

        // Student selector
        ComboBox<String> studentField = GlassComponents.glassComboBox("Select Student");
        try {
            for (Student s : studentService.getAllStudents()) {
                studentField.getItems().add(s.getId() + "|" + s.getStudentId() + " - " + s.getFullName());
            }
        } catch (Exception ignored) {}

        // Course selector
        ComboBox<String> courseField = GlassComponents.glassComboBox("Select Course");
        try {
            for (Course c : courseService.getAllCourses()) {
                courseField.getItems().add(c.getId() + "|" + c.getCourseCode() + " - " + c.getCourseName());
            }
        } catch (Exception ignored) {}

        TextField internalField = GlassComponents.glassTextField("Internal marks (0-40)");
        TextField externalField = GlassComponents.glassTextField("External marks (0-60)");

        ComboBox<String> semesterField = GlassComponents.glassComboBox("Semester");
        for (int i = 1; i <= 8; i++) semesterField.getItems().add(String.valueOf(i));

        TextField yearField = GlassComponents.glassTextField("e.g., 2025-26");

        // Auto-calculate preview
        Label previewLabel = new Label("");
        previewLabel.setStyle("-fx-text-fill: #22D3EE; -fx-font-size: 14px; -fx-font-weight: 600;");

        Runnable updatePreview = () -> {
            try {
                double internal = Double.parseDouble(internalField.getText());
                double external = Double.parseDouble(externalField.getText());
                double total = internal + external;
                String grade = GradeCalculator.calculateGrade(total);
                double gp = GradeCalculator.gradeToPoint(grade);
                previewLabel.setText(String.format("Total: %.0f | Grade: %s | GP: %.1f", total, grade, gp));
            } catch (NumberFormatException ignored) {
                previewLabel.setText("");
            }
        };
        internalField.textProperty().addListener((o, old, n) -> updatePreview.run());
        externalField.textProperty().addListener((o, old, n) -> updatePreview.run());

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setWrapText(true);

        Button saveBtn = GlassComponents.primaryButton("Save Marks", IconFactory.SAVE);
        saveBtn.setOnAction(e -> {
            try {
                Mark mark = new Mark();
                if (studentField.getValue() != null) mark.setStudentId(Integer.parseInt(studentField.getValue().split("\\|")[0]));
                if (courseField.getValue() != null) mark.setCourseId(Integer.parseInt(courseField.getValue().split("\\|")[0]));
                mark.setInternalMarks(Double.parseDouble(internalField.getText().trim()));
                mark.setExternalMarks(Double.parseDouble(externalField.getText().trim()));
                if (semesterField.getValue() != null) mark.setSemester(Integer.parseInt(semesterField.getValue()));
                mark.setAcademicYear(yearField.getText().trim());

                List<String> errors = resultService.saveMarks(mark);
                if (errors.isEmpty()) {
                    ToastNotification.success("Marks saved successfully");
                    loadContent(contentContainer);
                } else {
                    errorLabel.setText(String.join("\n", errors));
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                }
            } catch (NumberFormatException nfe) {
                ToastNotification.error("Please enter valid numeric marks");
            } catch (Exception ex) {
                ToastNotification.error("Error: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
            GlassComponents.formField("Student *", studentField),
            GlassComponents.formField("Course *", courseField),
            new HBox(12, GlassComponents.formField("Internal Marks (0-40) *", internalField),
                         GlassComponents.formField("External Marks (0-60) *", externalField)),
            previewLabel,
            new HBox(12, GlassComponents.formField("Semester *", semesterField),
                         GlassComponents.formField("Academic Year", yearField)),
            errorLabel, saveBtn
        );

        contentContainer.getChildren().addAll(header, form);
    }

    private void refreshTable() {
        new Thread(() -> {
            try {
                List<Mark> marks = resultService.getAllMarks(500, 0);
                Platform.runLater(() -> resultTable.setItems(FXCollections.observableArrayList(marks)));
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Failed to load results"));
            }
        }).start();
    }
}
