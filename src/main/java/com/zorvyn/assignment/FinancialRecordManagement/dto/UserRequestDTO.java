package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserRequestDTO", description = "The required data to create or update a user account in the system")
public class UserRequestDTO {

    @Schema(description = "The full name of the user", example = "John Doe", minLength = 2, maxLength = 50)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Schema(description = "A unique, valid email address for account identification", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Schema(description = "A 10-digit numeric mobile number", example = "9876543210")
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @Schema(
            description = "A secure password containing at least one uppercase letter, one lowercase, one number, and one special character.",
            example = "SecureP@ss123",
            minLength = 8,
            maxLength = 20
    )
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must include uppercase, lowercase, number, and special character"
    )
    private String password;

    @Schema(description = "The authorization level assigned to the user", example = "ADMIN")
    @NotNull(message = "Role is required")
    private Role role;

    @Schema(description = "Whether the account is currently active", example = "true")
    @NotNull(message = "Status must be specified")
    private Boolean status;

    @Schema(description = "A flag indicating if the record is soft-deleted", example = "false")
    @NotNull(message = "isDeleted flag must be specified")
    private Boolean isDeleted;
}