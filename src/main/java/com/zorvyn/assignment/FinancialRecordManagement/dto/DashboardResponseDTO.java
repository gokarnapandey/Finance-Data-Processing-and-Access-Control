package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Schema(name = "DashboardResponseDTO", description = "Aggregated snapshot of financial health, trends, and recent activities")
public class DashboardResponseDTO {

    @Schema(description = "Sum of all non-deleted income transactions", example = "5000.00")
    private BigDecimal totalIncome;

    @Schema(description = "Sum of all non-deleted expense transactions", example = "1200.50")
    private BigDecimal totalExpense;

    @Schema(description = "Calculated net balance (Income - Expense)", example = "3799.50")
    private BigDecimal netBalance;

    @Schema(
            description = "A mapping of Categories to their total monetary value. Useful for Pie/Donut charts.",
            example = "{ \"FOOD\": 150.00, \"RENT\": 1000.00 }"
    )
    private Map<Category, BigDecimal> categoryWiseTotals;

    @Schema(
            description = "Time-series data mapping months (format: YYYY-MM) to total transaction values. Ideal for Line/Area charts.",
            example = "{ \"2026-03\": 4500.00, \"2026-04\": 3800.00 }"
    )
    private Map<String, BigDecimal> monthlyTrends;

    @Schema(description = "A list of the 5 most recent financial movements for the 'Latest Activity' feed.")
    private List<RecentRecordDTO> recentRecords;
}