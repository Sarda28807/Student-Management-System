package com.novastudent.controller;

import com.novastudent.model.DashboardStats;
import com.novastudent.service.DashboardService;
import com.novastudent.ui.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

/**
 * Controller for the Analytics page.
 * Displays deeper insights and charts.
 */
public class AnalyticsController {

    private final DashboardService dashboardService;
    private VBox contentContainer;

    public AnalyticsController() {
        this.dashboardService = new DashboardService();
    }

    public void loadContent(VBox container) {
        this.contentContainer = container;
        container.getChildren().clear();

        Label loading = new Label("Generating analytics...");
        loading.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
        container.getChildren().add(loading);

        new Thread(() -> {
            try {
                DashboardStats stats = dashboardService.getDashboardStats();
                Platform.runLater(() -> buildAnalyticsView(stats));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    container.getChildren().clear();
                    ToastNotification.error("Failed to load analytics: " + e.getMessage());
                });
            }
        }).start();
    }

    private void buildAnalyticsView(DashboardStats stats) {
        contentContainer.getChildren().clear();

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10, 0, 0, 0));

        // GPA Distribution Chart (Line Chart)
        VBox gpaCard = GlassComponents.glassCard();
        Label gpaTitle = GlassComponents.sectionTitle("GPA Trend by Department");
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(10);
        yAxis.setTickUnit(1);
        
        LineChart<String, Number> gpaChart = new LineChart<>(xAxis, yAxis);
        gpaChart.setPrefHeight(300);
        gpaChart.setAnimated(true);
        
        // Mocking trend data for analytics view since we don't have historic GPA trend in DAO
        // In a real scenario, this would come from a dedicated AnalyticsDAO
        XYChart.Series<String, Number> seriesCS = new XYChart.Series<>();
        seriesCS.setName("Computer Science");
        seriesCS.getData().add(new XYChart.Data<>("Sem 1", 8.2));
        seriesCS.getData().add(new XYChart.Data<>("Sem 2", 8.4));
        seriesCS.getData().add(new XYChart.Data<>("Sem 3", 8.1));
        seriesCS.getData().add(new XYChart.Data<>("Sem 4", 8.6));
        
        XYChart.Series<String, Number> seriesIT = new XYChart.Series<>();
        seriesIT.setName("Information Tech");
        seriesIT.getData().add(new XYChart.Data<>("Sem 1", 7.9));
        seriesIT.getData().add(new XYChart.Data<>("Sem 2", 8.0));
        seriesIT.getData().add(new XYChart.Data<>("Sem 3", 8.3));
        seriesIT.getData().add(new XYChart.Data<>("Sem 4", 8.5));

        gpaChart.getData().addAll(seriesCS, seriesIT);
        gpaCard.getChildren().addAll(gpaTitle, gpaChart);

        // Second Row: Grade Spread & Demographics
        HBox row2 = new HBox(20);
        
        // Grade Spread (Bar Chart)
        VBox gradeCard = GlassComponents.glassCard();
        gradeCard.setPrefWidth(500);
        HBox.setHgrow(gradeCard, Priority.ALWAYS);
        Label gradeTitle = GlassComponents.sectionTitle("Overall Grade Distribution");
        
        CategoryAxis bXAxis = new CategoryAxis();
        NumberAxis bYAxis = new NumberAxis();
        BarChart<String, Number> gradeChart = new BarChart<>(bXAxis, bYAxis);
        gradeChart.setPrefHeight(250);
        gradeChart.setLegendVisible(false);
        
        XYChart.Series<String, Number> gradeSeries = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : stats.getGradeDistribution().entrySet()) {
            gradeSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        gradeChart.getData().add(gradeSeries);
        gradeCard.getChildren().addAll(gradeTitle, gradeChart);

        // Demographics (Pie Chart)
        VBox demoCard = GlassComponents.glassCard();
        demoCard.setPrefWidth(400);
        Label demoTitle = GlassComponents.sectionTitle("Student Status");
        
        PieChart statusChart = new PieChart();
        statusChart.setPrefHeight(250);
        statusChart.setLabelsVisible(true);
        statusChart.getData().add(new PieChart.Data("Active", stats.getActiveStudents()));
        statusChart.getData().add(new PieChart.Data("Graduated", stats.getGraduatedStudents()));
        statusChart.getData().add(new PieChart.Data("Inactive/Suspended", stats.getTotalStudents() - stats.getActiveStudents() - stats.getGraduatedStudents()));
        
        demoCard.getChildren().addAll(demoTitle, statusChart);
        
        row2.getChildren().addAll(gradeCard, demoCard);

        layout.getChildren().addAll(gpaCard, row2);
        contentContainer.getChildren().add(layout);
    }
}
