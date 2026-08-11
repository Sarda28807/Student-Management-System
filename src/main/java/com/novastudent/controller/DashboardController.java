package com.novastudent.controller;

import com.novastudent.model.DashboardStats;
import com.novastudent.model.Student;
import com.novastudent.service.DashboardService;
import com.novastudent.ui.*;
import com.novastudent.util.DateUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * Dashboard controller — the main overview page.
 * Shows statistics, charts, recent activity, and top performers.
 */
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController() {
        this.dashboardService = new DashboardService();
    }

    /**
     * Loads dashboard content into the container.
     */
    public void loadContent(VBox container) {
        container.getChildren().clear();

        Label loading = new Label("Loading dashboard...");
        loading.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
        container.getChildren().add(loading);

        // Load data async
        new Thread(() -> {
            try {
                DashboardStats stats = dashboardService.getDashboardStats();
                Platform.runLater(() -> buildDashboard(container, stats));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    container.getChildren().clear();
                    container.getChildren().add(buildErrorState("Failed to load dashboard: " + e.getMessage()));
                    ToastNotification.error("Dashboard load failed: " + e.getMessage());
                });
            }
        }).start();
    }

    private void buildDashboard(VBox container, DashboardStats stats) {
        container.getChildren().clear();

        // ===== ROW 1: Stat Cards =====
        HBox statRow = new HBox(16);
        statRow.setAlignment(Pos.CENTER_LEFT);

        VBox totalCard = GlassComponents.statCard(
            IconFactory.STUDENTS, "TOTAL STUDENTS",
            formatNumber(stats.getTotalStudents()),
            (stats.getStudentGrowthPercent() >= 0 ? "+" : "") + stats.getStudentGrowthPercent() + "%",
            Color.web("#8B5CF6"));
        HBox.setHgrow(totalCard, Priority.ALWAYS);

        VBox activeCard = GlassComponents.statCard(
            IconFactory.CHECK, "ACTIVE STUDENTS",
            formatNumber(stats.getActiveStudents()),
            stats.getActiveStudents() + " of " + stats.getTotalStudents(),
            Color.web("#22D3EE"));
        HBox.setHgrow(activeCard, Priority.ALWAYS);

        VBox presentCard = GlassComponents.statCard(
            IconFactory.ATTENDANCE, "PRESENT TODAY",
            formatNumber(stats.getPresentToday()),
            "Today's attendance",
            Color.web("#3B82F6"));
        HBox.setHgrow(presentCard, Priority.ALWAYS);

        VBox rateCard = GlassComponents.statCard(
            IconFactory.ANALYTICS, "ATTENDANCE RATE",
            String.format("%.1f%%", stats.getAttendanceRate()),
            "Last 30 days",
            Color.web("#22C55E"));
        HBox.setHgrow(rateCard, Priority.ALWAYS);

        statRow.getChildren().addAll(totalCard, activeCard, presentCard, rateCard);

        // ===== ROW 2: Charts =====
        HBox chartRow = new HBox(16);
        chartRow.setAlignment(Pos.TOP_LEFT);

        // Attendance Trend Chart
        VBox attendanceChartCard = GlassComponents.glassCard();
        attendanceChartCard.setPrefWidth(600);
        HBox.setHgrow(attendanceChartCard, Priority.ALWAYS);

        Label chartTitle1 = new Label("Attendance Overview");
        chartTitle1.getStyleClass().add("text-section");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("");
        yAxis.setLabel("");

        BarChart<String, Number> attendanceChart = new BarChart<>(xAxis, yAxis);
        attendanceChart.setLegendVisible(true);
        attendanceChart.setAnimated(true);
        attendanceChart.setPrefHeight(280);

        XYChart.Series<String, Number> presentSeries = new XYChart.Series<>();
        presentSeries.setName("Present");
        XYChart.Series<String, Number> absentSeries = new XYChart.Series<>();
        absentSeries.setName("Absent");
        XYChart.Series<String, Number> lateSeries = new XYChart.Series<>();
        lateSeries.setName("Late");

        Map<String, int[]> trend = stats.getAttendanceTrend();
        for (Map.Entry<String, int[]> entry : trend.entrySet()) {
            String date = entry.getKey().substring(5); // MM-DD
            int[] vals = entry.getValue();
            presentSeries.getData().add(new XYChart.Data<>(date, vals[0]));
            absentSeries.getData().add(new XYChart.Data<>(date, vals[1]));
            lateSeries.getData().add(new XYChart.Data<>(date, vals[2]));
        }

        attendanceChart.getData().addAll(presentSeries, absentSeries, lateSeries);
        attendanceChartCard.getChildren().addAll(chartTitle1, attendanceChart);

        // Department Distribution Pie Chart
        VBox deptChartCard = GlassComponents.glassCard();
        deptChartCard.setPrefWidth(350);

        Label chartTitle2 = new Label("Department Distribution");
        chartTitle2.getStyleClass().add("text-section");

        PieChart deptChart = new PieChart();
        deptChart.setAnimated(true);
        deptChart.setPrefHeight(280);
        deptChart.setLegendVisible(true);
        deptChart.setLabelsVisible(false);

        for (Map.Entry<String, Integer> entry : stats.getDepartmentDistribution().entrySet()) {
            deptChart.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        deptChartCard.getChildren().addAll(chartTitle2, deptChart);

        chartRow.getChildren().addAll(attendanceChartCard, deptChartCard);

        // ===== ROW 3: Widgets =====
        HBox widgetRow = new HBox(16);
        widgetRow.setAlignment(Pos.TOP_LEFT);

        // Top Performers
        VBox performersCard = GlassComponents.glassCard();
        performersCard.setPrefWidth(400);
        HBox.setHgrow(performersCard, Priority.ALWAYS);

        Label perfTitle = new Label("Top Performers");
        perfTitle.getStyleClass().add("text-section");
        performersCard.getChildren().add(perfTitle);

        int rank = 1;
        for (String[] performer : stats.getTopPerformers()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 12, 8, 12));
            row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 8;");

            Label rankLabel = new Label(String.format("%02d", rank++));
            rankLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px; -fx-font-weight: 700;");
            rankLabel.setMinWidth(28);

            StackPane avatar = GlassComponents.avatar(
                performer[0].length() >= 2 ? performer[0].substring(0, 2).toUpperCase() : "??", 32);

            Label nameLabel = new Label(performer[0]);
            nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px; -fx-font-weight: 500;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label gpaLabel = new Label(performer[1] + " GPA");
            gpaLabel.setStyle("-fx-text-fill: #22C55E; -fx-font-size: 13px; -fx-font-weight: 600;");

            row.getChildren().addAll(rankLabel, avatar, nameLabel, gpaLabel);
            performersCard.getChildren().add(row);
        }

        // Recent Students
        VBox recentCard = GlassComponents.glassCard();
        recentCard.setPrefWidth(400);
        HBox.setHgrow(recentCard, Priority.ALWAYS);

        Label recentTitle = new Label("Recent Students");
        recentTitle.getStyleClass().add("text-section");
        recentCard.getChildren().add(recentTitle);

        for (Student student : stats.getRecentStudents()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 12, 8, 12));
            row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 8;");

            StackPane avatar = GlassComponents.avatar(student.getInitials(), 32);

            VBox info = new VBox(2);
            Label nameLabel = new Label(student.getFullName());
            nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px; -fx-font-weight: 500;");
            Label idLabel = new Label(student.getStudentId() + " • " +
                (student.getDepartmentCode() != null ? student.getDepartmentCode() : ""));
            idLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
            info.getChildren().addAll(nameLabel, idLabel);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label statusBadge = GlassComponents.statusBadge(student.getStatus().name());

            row.getChildren().addAll(avatar, info, statusBadge);
            recentCard.getChildren().add(row);
        }

        // Quick Stats
        VBox quickCard = GlassComponents.glassCard();
        quickCard.setPrefWidth(250);

        Label quickTitle = new Label("Quick Stats");
        quickTitle.getStyleClass().add("text-section");
        quickCard.getChildren().add(quickTitle);

        addQuickStat(quickCard, "Total Courses", String.valueOf(stats.getTotalCourses()), "#3B82F6");
        addQuickStat(quickCard, "Departments", String.valueOf(stats.getTotalDepartments()), "#8B5CF6");
        addQuickStat(quickCard, "Average GPA", String.format("%.2f", stats.getAverageGPA()), "#22D3EE");
        addQuickStat(quickCard, "Graduated", String.valueOf(stats.getGraduatedStudents()), "#22C55E");
        addQuickStat(quickCard, "New This Month", String.valueOf(stats.getNewStudentsThisMonth()), "#F59E0B");

        widgetRow.getChildren().addAll(performersCard, recentCard, quickCard);

        // ===== ROW 4: Grade Distribution =====
        VBox gradeCard = GlassComponents.glassCard();

        Label gradeTitle = new Label("Grade Distribution");
        gradeTitle.getStyleClass().add("text-section");

        CategoryAxis gradeXAxis = new CategoryAxis();
        NumberAxis gradeYAxis = new NumberAxis();
        BarChart<String, Number> gradeChart = new BarChart<>(gradeXAxis, gradeYAxis);
        gradeChart.setLegendVisible(false);
        gradeChart.setAnimated(true);
        gradeChart.setPrefHeight(220);

        XYChart.Series<String, Number> gradeSeries = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : stats.getGradeDistribution().entrySet()) {
            gradeSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        gradeChart.getData().add(gradeSeries);

        gradeCard.getChildren().addAll(gradeTitle, gradeChart);

        // Add all rows to container
        container.getChildren().addAll(statRow, chartRow, widgetRow, gradeCard);
    }

    private void addQuickStat(VBox container, String label, String value, String color) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 8;");

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");
        valLabel.setMinWidth(50);

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        row.getChildren().addAll(valLabel, nameLabel);
        container.getChildren().add(row);
    }

    private String formatNumber(int num) {
        if (num >= 1000) {
            return String.format("%,d", num);
        }
        return String.valueOf(num);
    }

    private VBox buildErrorState(String message) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));

        Label errorIcon = new Label("\u26A0");
        errorIcon.setStyle("-fx-font-size: 36px; -fx-text-fill: #F59E0B;");

        Label errorMsg = new Label(message);
        errorMsg.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
        errorMsg.setWrapText(true);

        box.getChildren().addAll(errorIcon, errorMsg);
        return box;
    }
}
