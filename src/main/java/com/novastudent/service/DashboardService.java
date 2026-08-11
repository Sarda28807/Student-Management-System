package com.novastudent.service;

import com.novastudent.dao.DashboardDAO;
import com.novastudent.model.DashboardStats;

import java.sql.SQLException;

/**
 * Service layer for dashboard data aggregation.
 */
public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAO();
    }

    /**
     * Loads all dashboard statistics.
     */
    public DashboardStats getDashboardStats() throws SQLException {
        return dashboardDAO.loadDashboardStats();
    }
}
