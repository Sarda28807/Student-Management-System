package com.novastudent.controller;

import com.novastudent.service.ReportService;
import com.novastudent.ui.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.awt.Desktop;

/**
 * Controller for generating and exporting PDF/CSV reports.
 */
public class ReportController {

    private final ReportService reportService;
    private VBox contentContainer;

    public ReportController() {
        this.reportService = new ReportService();
    }

    public void loadContent(VBox container) {
        this.contentContainer = container;
        container.getChildren().clear();

        // Main layout
        VBox layout = new VBox(24);
        layout.setPadding(new Insets(10, 0, 0, 0));

        Label title = new Label("Export & Reports");
        title.getStyleClass().add("text-heading");

        Label subtitle = new Label("Generate tabular reports in CSV or formatted PDF formats. Files are saved in the 'exports' directory.");
        subtitle.getStyleClass().add("text-body");

        // Cards layout
        FlowPane grid = new FlowPane(20, 20);
        
        grid.getChildren().addAll(
            buildReportCard("Student Master List", "Export a complete list of all registered students with personal and academic details.", "studentList"),
            buildReportCard("Attendance Log", "Export recent attendance records for all courses and students.", "attendance"),
            buildReportCard("Academic Results", "Export full semester results including internal, external marks and calculated GPA.", "results"),
            buildReportCard("Course Catalog", "Export the list of all available courses, credits, and their assigned departments.", "courses")
        );

        layout.getChildren().addAll(title, subtitle, grid);
        container.getChildren().add(layout);
    }

    private VBox buildReportCard(String title, String description, String type) {
        VBox card = GlassComponents.glassCard();
        card.setPrefWidth(350);
        card.setPrefHeight(220);
        card.setPadding(new Insets(24));
        card.setSpacing(12);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        StackPane iconBg = new StackPane();
        iconBg.setMinSize(44, 44);
        iconBg.setMaxSize(44, 44);
        iconBg.setStyle("-fx-background-color: rgba(139, 92, 246, 0.15); -fx-background-radius: 12;");
        iconBg.getChildren().add(GlassComponents.createIcon(IconFactory.REPORTS, 22, Color.web("#8B5CF6")));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #F8FAFC;");

        header.getChildren().addAll(iconBg, titleLabel);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8;");
        descLabel.setWrapText(true);
        descLabel.setPrefHeight(60);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button csvBtn = GlassComponents.secondaryButton("CSV", null);
        csvBtn.setOnAction(e -> handleExport(type, "csv"));
        
        Button pdfBtn = GlassComponents.primaryButton("PDF", IconFactory.PDF);
        pdfBtn.setOnAction(e -> handleExport(type, "pdf"));

        buttonRow.getChildren().addAll(csvBtn, pdfBtn);

        card.getChildren().addAll(header, descLabel, spacer, buttonRow);
        return card;
    }

    private void handleExport(String type, String format) {
        new Thread(() -> {
            try {
                String path = "";
                if ("csv".equals(format)) {
                    switch (type) {
                        case "studentList": path = reportService.exportStudentListCSV(); break;
                        case "attendance": path = reportService.exportAttendanceCSV(); break;
                        case "results": path = reportService.exportResultsCSV(); break;
                        case "courses": path = reportService.exportCoursesCSV(); break;
                    }
                } else if ("pdf".equals(format)) {
                    switch (type) {
                        case "studentList": path = reportService.exportStudentListPDF(); break;
                        case "attendance": path = reportService.exportAttendancePDF(); break;
                        case "results": path = reportService.exportResultsPDF(); break;
                        // Courses PDF not implemented in ReportService yet, fallback to CSV behavior message if not found
                    }
                }

                if (path.isEmpty()) {
                    Platform.runLater(() -> ToastNotification.warning("Export format currently unavailable for this report type."));
                    return;
                }

                final String finalPath = path;
                Platform.runLater(() -> {
                    ToastNotification.success(format.toUpperCase() + " Exported to: " + finalPath);
                    openFileLocation(finalPath);
                });
            } catch (Exception e) {
                Platform.runLater(() -> ToastNotification.error("Export Failed: " + e.getMessage()));
            }
        }).start();
    }

    private void openFileLocation(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && Desktop.isDesktopSupported()) {
                // Open the folder containing the file
                Desktop.getDesktop().open(file.getParentFile());
            }
        } catch (Exception e) {
            // Ignore if unable to open explorer
        }
    }
}
