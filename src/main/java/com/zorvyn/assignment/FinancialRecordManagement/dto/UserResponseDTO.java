package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(name = "UserResponseDTO", description = "Public profile and account status information for a user")
public class UserResponseDTO {

    @Schema(description = "Unique business identifier for the user", example = "USR-99210")
    private String userId;

    @Schema(description = "Full legal name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Primary email address used for login", example = "john.doe@example.com")
    private String email;

    @Schema(description = "10-digit mobile contact number", example = "9876543210")
    private String mobileNumber;

    @Schema(description = "Assigned authorization level in the system", example = "ADMIN")
    private Role role;

    @Schema(description = "Current account activation status", example = "true")
    private Boolean status;

    @Schema(description = "Soft-deletion flag for record retention", example = "false")
    private Boolean isDeleted;

    @Schema(description = "The timestamp when the user account was first created")
    private LocalDateTime createdAt;

    @Schema(description = "The timestamp of the most recent profile or status update")
    private LocalDateTime updatedAt;
}