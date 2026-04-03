package com.zorvyn.assignment.FinancialRecordManagement.service;

import com.zorvyn.assignment.FinancialRecordManagement.dto.DashboardResponseDTO;


import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.dto.RecentRecordDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashBoardService {

    /**
     * Requirement 3: Provide high-level summary data.
     * Returns the consolidated DashboardResponseDTO containing totals, trends, and recent records.
     */
    DashboardResponseDTO getSummary();

    /**
     * Requirement 3: Category wise totals.
     * Returns a map of totals grouped by Category.
     */
    Map<Category, BigDecimal> getCategoryWiseTotals();

    /**
     * Requirement 3: Monthly or weekly trends.
     * Returns a map where the key is the month (e.g., "2026-04") and the value is the total amount.
     */
    Map<String, BigDecimal> getMonthlyTrends();

    /**
     * Requirement 3: Recent activity.
     * Returns a list of the most recent financial entries using the lightweight RecentRecordDTO.
     */
    List<RecentRecordDTO> getRecentActivities();

}
