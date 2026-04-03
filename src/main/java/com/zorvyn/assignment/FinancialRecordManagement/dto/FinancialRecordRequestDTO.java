package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "FinancialRecordRequestDTO", description = "The data required to create or update a financial transaction record")
public class FinancialRecordRequestDTO {

    @Schema(description = "The monetary value of the transaction. Must be greater than zero.", example = "150.75", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount should be a valid positive number")
    private BigDecimal amount;

    @Schema(description = "The type of movement: INCOME (Money in) or EXPENSE (Money out)", example = "EXPENSE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Choose your transaction type : INCOME, EXPENSE ")
    private TransactionType transactionType;

    @Schema(description = "The category classification for the transaction", example = "FOOD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Choose your category of transaction: FOOD, RENT, TRANSPORT, ENTERTAINMENT, SALARY, INVESTMENT, OTHER")
    private Category category;

    @Schema(description = "Brief details or notes regarding the transaction", example = "Dinner at Italian Restaurant", maxLength = 255)
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be under 255 characters")
    private String description;
}