package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(name = "RecentRecordDTO", description = "A lightweight view of a financial transaction for dashboard activity feeds")
public class RecentRecordDTO {

    @Schema(description = "Unique identifier for the financial record", example = "REC-55421")
    private String financialRecordId;

    @Schema(description = "The monetary value of the transaction", example = "45.99")
    private BigDecimal amount;

    @Schema(description = "The type of movement: INCOME or EXPENSE", example = "EXPENSE")
    private TransactionType transactionType;

    @Schema(description = "The classification category for the record", example = "TRANSPORT")
    private Category category;

    @Schema(description = "A brief summary or note about the transaction", example = "Uber ride to office")
    private String description;

    @Schema(description = "The timestamp indicating when the activity occurred")
    private LocalDateTime createdAt;
}