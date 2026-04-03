package com.zorvyn.assignment.FinancialRecordManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(name = "PaginatedResponseDTO", description = "Generic wrapper for paginated result sets, providing navigation metadata")
public class PaginatedResponseDTO<T> {

    @Schema(description = "The list of data records for the current page slice")
    private List<T> content;

    @Schema(description = "The current zero-based page index", example = "0")
    private int page;

    @Schema(description = "The number of items requested per page", example = "10")
    private int size;

    @Schema(description = "The total count of all records matching the query across all pages", example = "150")
    private long totalElements;

    @Schema(description = "The total number of pages available based on the size provided", example = "15")
    private int totalPages;

    @Schema(description = "Indicates if this is the final page in the result set", example = "false")
    private boolean isLast;
}