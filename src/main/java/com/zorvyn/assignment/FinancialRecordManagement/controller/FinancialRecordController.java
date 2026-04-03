package com.zorvyn.assignment.FinancialRecordManagement.controller;


import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.PaginatedResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(
        name = "Financial Record Management",
        description = "Endpoints for creating, filtering, and managing financial transactions. " +
                "Supports advanced querying by category, date ranges, and transaction types."
)
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @Operation(
            summary = "Create a new financial record",
            description = "Adds a new transaction to the system. Validates amount, category, and date." +
                    "Type should be one of these: INCOME/EXPENSE," +
                    "Category should be one of these: FOOD, RENT, TRANSPORT, ENTERTAINMENT, SALARY, INVESTMENT, OTHER"
    )
    @PostMapping
    public ResponseEntity<FinancialRecordResponseDTO> createRecord(@Valid @RequestBody FinancialRecordRequestDTO request) {
        return new ResponseEntity<>(financialRecordService.createRecord(request), HttpStatus.CREATED);
    }


    @Operation(
            summary = "Get record by business ID",
            description = "Requirement 2: Retrieves full details of a specific financial record using its unique identifier."
    )
    @GetMapping("/{recordId}")
    public ResponseEntity<FinancialRecordResponseDTO> getRecordById(@PathVariable String recordId) {
        return ResponseEntity.ok(financialRecordService.getRecordById(recordId));
    }


    @Operation(
            summary = "List all records (Paginated)",
            description = "Requirement 2: Returns a paginated list of all active records. Use 'page' and 'size' to navigate."
    )
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<FinancialRecordResponseDTO>> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(financialRecordService.getAllRecords(page, size));
    }


    @Operation(
            summary = "Advanced record filtering",
            description = "Requirement 2 & 5: Filter financial records based on Category (e.g., FOOD, RENT), " +
                    "Transaction Type (e.g., INCOME, EXPENSE), and specific date ranges. " +
                    "Results are paginated to ensure optimal performance."
    )
    @GetMapping("/filter")
    public ResponseEntity<PaginatedResponseDTO<FinancialRecordResponseDTO>> filterRecords(
            @Parameter(description = "Filter by category. Available options are: FOOD, RENT, TRANSPORT, ENTERTAINMENT, SALARY, INVESTMENT, OTHER")
            @RequestParam(required = false) Category category,
            @Parameter(description = "Filter by category. Available options are: INCOME, EXPENSE")
            @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Start date for the range (inclusive). Format: yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for the range (inclusive). Format: yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(financialRecordService.filterRecords(category, type, startDate, endDate, page, size));
    }


    @Operation(
            summary = "Update an existing record",
            description = "Requirement 2: Modifies an existing transaction. Only available to users with ADMIN role."
    )
    @PutMapping("/{recordId}")
    public ResponseEntity<FinancialRecordResponseDTO> updateRecord(
            @PathVariable String recordId,
            @Valid @RequestBody FinancialRecordRequestDTO request) {
        return ResponseEntity.ok(financialRecordService.updateRecord(recordId, request));
    }


    @Operation(
            summary = "Soft delete a record",
            description = "Requirement 5: Marks a financial record as deleted. The record remains in the database for audit history but is hidden from standard views."
    )
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String recordId) {
        financialRecordService.deleteRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}
