package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "FinancialRecordResponseDTO", description = "Detailed view of a financial transaction record as stored in the system")
public class FinancialRecordResponseDTO {

    @Schema(description = "Unique business identifier for the financial record", example = "REC-77382")
    private String financialRecordId;

    @Schema(description = "The monetary value of the transaction", example = "2500.00")
    private BigDecimal amount;

    @Schema(description = "The movement type: INCOME or EXPENSE", example = "INCOME")
    private TransactionType transactionType;

    @Schema(description = "The classified category for the record", example = "SALARY")
    private Category category;

    @Schema(description = "Narrative description of the transaction", example = "Monthly freelance payment")
    private String description;

    @Schema(description = "The email or username of the account that created this record", example = "system@admin.com")
    private String createBy;

    @Schema(description = "The exact date and time the record was persisted")
    private LocalDateTime createdAt;

    @Schema(description = "The exact date and time of the most recent update to this record")
    private LocalDateTime updatedAt;
}