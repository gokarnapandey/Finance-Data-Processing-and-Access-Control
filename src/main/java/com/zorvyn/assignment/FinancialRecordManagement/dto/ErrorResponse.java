package com.zorvyn.assignment.FinancialRecordManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(name = "ErrorResponse", description = "Standardized structure for API error messages")
public class ErrorResponse {

    @Schema(description = "Timestamp of when the error occurred", example = "2026-04-03T13:26:22")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Short name of the error type", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed explanation of the error", example = "Password must include uppercase, lowercase, number, and special character")
    private String message;

    @Schema(description = "The API endpoint path where the error occurred", example = "/api/v1/users")
    private String path;

}