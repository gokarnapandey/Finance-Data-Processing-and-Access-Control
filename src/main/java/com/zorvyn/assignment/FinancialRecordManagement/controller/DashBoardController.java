package com.zorvyn.assignment.FinancialRecordManagement.controller;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.dto.DashboardResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.RecentRecordDTO;
import com.zorvyn.assignment.FinancialRecordManagement.service.DashBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard & Analytics",
        description = "Read-only endpoints for high-performance data aggregation. " +
                "Provides summarized financial views, trends, and recent activity for reporting."
)
public class DashBoardController {

    private final DashBoardService dashBoardService;

    @Operation(
            summary = "Get full dashboard state",
            description = "Requirement 3: Fetches a comprehensive snapshot of total balance, income vs. expenses, " +
                    "and key metrics in a single high-performance request."
    )
    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getFullDashboard() {
        return ResponseEntity.ok(dashBoardService.getSummary());
    }


    @Operation(
            summary = "Get totals by category",
            description = "Aggregates financial data into category-wise totals (e.g., Food, Rent, Salary). " +
                    "Ideal for rendering pie charts or breakdown lists."
    )
    @GetMapping("/categories")
    public ResponseEntity<Map<Category, BigDecimal>> getCategoryTotals() {
        return ResponseEntity.ok(dashBoardService.getCategoryWiseTotals());
    }


    @Operation(
            summary = "Get monthly financial trends",
            description = "Provides time-series data mapping months to total transaction values. " +
                    "Designed for line or bar charts to show financial growth or spending over time."
    )
    @GetMapping("/trends")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyTrends() {
        return ResponseEntity.ok(dashBoardService.getMonthlyTrends());
    }


    @Operation(
            summary = "Get recent activities",
            description = "Retrieves the 5 most recent transactions to provide a quick glance at the latest financial movements."
    )
    @GetMapping("/recent")
    public ResponseEntity<List<RecentRecordDTO>> getRecentActivity() {
        return ResponseEntity.ok(dashBoardService.getRecentActivities());
    }
}