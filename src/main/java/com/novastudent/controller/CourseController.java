package com.novastudent.controller;

import com.novastudent.model.Course;
import com.novastudent.model.Department;
import com.novastudent.service.CourseService;
import com.novastudent.ui.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Controller for courses and departments management.
 */
public class CourseController {

    private final CourseService courseService;
    private TableView<Course> courseTable;
    private VBox contentContainer;

    public CourseController() {
        this.courseService = new CourseService();
    }

    public void loadContent(VBox container) {
        this.contentContainer = container;
        container.getChildren().clear();

        // Tabs for Courses and Departments
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("glass-tab-pane");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab coursesTab = new Tab("Courses");
        coursesTab.setContent(buildCoursesTab());

        Tab deptsTab = new Tab("Departments");
        deptsTab.setContent(buildDepartmentsTab());

        tabPane.getTabs().addAll(coursesTab, deptsTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        container.getChildren().add(tabPane);
    }

    @SuppressWarnings("unchecked")
    private VBox buildCoursesTab() {
        VBox tab = new VBox(16);
        tab.setPadding(new Insets(16, 0, 0, 0));

        // Action bar
        HBox actionBar = new HBox(12);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = GlassComponents.searchField("Search courses...");
        searchField.setPrefWidth(250);

        ComboBox<String> deptFilter = GlassComponents.glassComboBox("Department");
        deptFilter.setPrefWidth(150);
        deptFilter.getItems().add("All");
        try {
            for (Department d : courseService.getAllDepartments()) {
                deptFilter.getItems().add(d.getDepartmentCode());
            }
        } catch (Exception ignored) {}
        deptFilter.setValue("All");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = GlassComponents.primaryButton("Add Course", IconFactory.ADD);
        addBtn.setOnAction(e -> showCourseForm(null));

        actionBar.getChildren().addAll(searchField, deptFilter, spacer, addBtn);

        // Table
        courseTable = GlassComponents.glassTable();
        courseTable.setPrefHeight(450);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        codeCol.setPrefWidth(120);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        nameCol.setPrefWidth(250);

        TableColumn<Course, String> creditsCol = new TableColumn<>("Credits");
        creditsCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCredits())));
        creditsCol.setPrefWidth(80);

        TableColumn<Course, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDepartmentCode() != null ? data.getValue().getDepartmentCode() : "—"));
        deptCol.setPrefWidth(120);

        TableColumn<Course, String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(data -> new SimpleStringProperty("Sem " + data.getValue().getSemester()));
        semCol.setPrefWidth(90);

        TableColumn<Course, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(140);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = GlassComponents.smallButton("Edit", null);
            final Button deleteBtn = GlassComponents.smallButton("Delete", "glass-button-danger");
            final HBox box = new HBox(6, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                editBtn.setOnAction(e -> {
                    Course c = getTableRow().getItem();
                    if (c != null) showCourseForm(c);
                });
                deleteBtn.setOnAction(e -> {
                    Course c = getTableRow().getItem();
                    if (c != null) confirmDeleteCourse(c);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        courseTable.getColumns().addAll(codeCol, nameCol, creditsCol, deptCol, semCol, actionsCol);

        // Search action
        searchField.setOnAction(e -> {
            try {
                courseTable.setItems(FXCollections.observableArrayList(courseService.searchCourses(searchField.getText())));
            } catch (Exception ex) {
                ToastNotification.error("Search failed: " + ex.getMessage());
            }
        });

        // Filter action
        deptFilter.setOnAction(e -> {
            try {
                String val = deptFilter.getValue();
                if ("All".equals(val)) {
                    courseTable.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
                } else {
                    List<Department> depts = courseService.getAllDepartments();
                    for (Department d : depts) {
                        if (d.getDepartmentCode().equals(val)) {
                            courseTable.setItems(FXCollections.observableArrayList(courseService.getCoursesByDepartment(d.getId())));
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ToastNotification.error("Filter failed: " + ex.getMessage());
            }
        });

        tab.getChildren().addAll(actionBar, courseTable);
        VBox.setVgrow(courseTable, Priority.ALWAYS);

        // Load data
        refreshCourses();
        return tab;
    }

    @SuppressWarnings("unchecked")
    private VBox buildDepartmentsTab() {
        VBox tab = new VBox(16);
        tab.setPadding(new Insets(16, 0, 0, 0));

        TableView<Department> deptTable = GlassComponents.glassTable();
        deptTable.setPrefHeight(450);

        TableColumn<Department, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("departmentCode"));
        codeCol.setPrefWidth(100);

        TableColumn<Department, String> nameCol = new TableColumn<>("Department Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        nameCol.setPrefWidth(300);

        TableColumn<Department, String> hodCol = new TableColumn<>("HOD");
        hodCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getHodName() != null ? data.getValue().getHodName() : "—"));
        hodCol.setPrefWidth(200);

        TableColumn<Department, String> studentsCol = new TableColumn<>("Students");
        studentsCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStudentCount())));
        studentsCol.setPrefWidth(100);

        TableColumn<Department, String> coursesCol = new TableColumn<>("Courses");
        coursesCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCourseCount())));
        coursesCol.setPrefWidth(100);

        deptTable.getColumns().addAll(codeCol, nameCol, hodCol, studentsCol, coursesCol);

        tab.getChildren().add(deptTable);
        VBox.setVgrow(deptTable, Priority.ALWAYS);

        // Load data
        new Thread(() -> {
            try {
                List<Department> departments = courseService.getAllDepartments();
                Platform.runLater(() -> deptTable.setItems(FXCollections.observableArrayList(departments)));
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Failed to load departments"));
            }
        }).start();

        return tab;
    }

    private void showCourseForm(Course existing) {
        contentContainer.getChildren().clear();
        boolean isEdit = existing != null;

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = GlassComponents.secondaryButton("Back", IconFactory.CLOSE);
        backBtn.setOnAction(e -> loadContent(contentContainer));
        Label title = new Label(isEdit ? "Edit Course" : "Add New Course");
        title.getStyleClass().add("text-heading");
        header.getChildren().addAll(backBtn, title);

        VBox form = GlassComponents.glassPanel();
        form.setMaxWidth(600);
        form.setSpacing(16);

        TextField codeField = GlassComponents.glassTextField("e.g., CS401");
        TextField nameField = GlassComponents.glassTextField("Course Name");
        ComboBox<String> creditsField = GlassComponents.glassComboBox("Credits");
        creditsField.getItems().addAll("1", "2", "3", "4", "5", "6");

        ComboBox<String> deptField = GlassComponents.glassComboBox("Department");
        try {
            for (Department d : courseService.getAllDepartments()) {
                deptField.getItems().add(d.getId() + "|" + d.getDepartmentCode() + " - " + d.getDepartmentName());
            }
        } catch (Exception ignored) {}

        ComboBox<String> semField = GlassComponents.glassComboBox("Semester");
        semField.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8");

        TextArea descField = GlassComponents.glassTextArea("Course description", 3);

        if (isEdit) {
            codeField.setText(existing.getCourseCode());
            nameField.setText(existing.getCourseName());
            creditsField.setValue(String.valueOf(existing.getCredits()));
            semField.setValue(String.valueOf(existing.getSemester()));
            descField.setText(existing.getDescription());
            for (String item : deptField.getItems()) {
                if (item.startsWith(existing.getDepartmentId() + "|")) { deptField.setValue(item); break; }
            }
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setWrapText(true);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = GlassComponents.secondaryButton("Cancel", null);
        cancelBtn.setOnAction(e -> loadContent(contentContainer));
        Button saveBtn = GlassComponents.primaryButton(isEdit ? "Update Course" : "Create Course", IconFactory.SAVE);
        saveBtn.setOnAction(e -> {
            try {
                Course course = isEdit ? existing : new Course();
                course.setCourseCode(codeField.getText().trim());
                course.setCourseName(nameField.getText().trim());
                course.setCredits(creditsField.getValue() != null ? Integer.parseInt(creditsField.getValue()) : 0);
                course.setSemester(semField.getValue() != null ? Integer.parseInt(semField.getValue()) : 0);
                course.setDescription(descField.getText().trim());
                if (deptField.getValue() != null) {
                    course.setDepartmentId(Integer.parseInt(deptField.getValue().split("\\|")[0]));
                }

                List<String> errors;
                if (isEdit) errors = courseService.updateCourse(course);
                else errors = courseService.createCourse(course);

                if (errors.isEmpty()) {
                    ToastNotification.success(isEdit ? "Course updated" : "Course created");
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

        form.getChildren().addAll(
            GlassComponents.formField("Course Code *", codeField),
            GlassComponents.formField("Course Name *", nameField),
            new HBox(12, GlassComponents.formField("Credits *", creditsField), GlassComponents.formField("Semester *", semField)),
            GlassComponents.formField("Department *", deptField),
            GlassComponents.formField("Description", descField),
            errorLabel, btnRow
        );

        contentContainer.getChildren().addAll(header, form);
    }

    private void refreshCourses() {
        new Thread(() -> {
            try {
                List<Course> courses = courseService.getAllCourses();
                Platform.runLater(() -> courseTable.setItems(FXCollections.observableArrayList(courses)));
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Failed to load courses"));
            }
        }).start();
    }

    private void confirmDeleteCourse(Course course) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setHeaderText("Delete " + course.getCourseName() + "?");
        alert.setContentText("This will also delete related attendance and marks records.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (courseService.deleteCourse(course.getId())) {
                        ToastNotification.success("Course deleted");
                        refreshCourses();
                    }
                } catch (Exception e) {
                    ToastNotification.error("Error: " + e.getMessage());
                }
            }
        });
    }
}
