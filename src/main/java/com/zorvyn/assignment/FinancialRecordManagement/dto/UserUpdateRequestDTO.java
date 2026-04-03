package com.zorvyn.assignment.FinancialRecordManagement.dto;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserUpdateRequestDTO", description = "The data fields that can be modified for an existing user account")
public class UserUpdateRequestDTO {

    @Schema(description = "Updated full name of the user", example = "John Updated Doe", minLength = 2, maxLength = 50)
    @Size(min = 2, max = 50)
    private String name;

    @Schema(description = "Updated 10-digit mobile contact number", example = "9988776655")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @Schema(
            description = "New secure password. If provided, must meet complexity requirements (Upper, Lower, Number, Special).",
            example = "NewSecureP@ss123",
            minLength = 8,
            maxLength = 20
    )
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must include uppercase, lowercase, number, and special character"
    )
    private String password;

    @Schema(description = "Updated account activation status", example = "true")
    private Boolean status;

    @Schema(description = "Modified authorization level in the system", example = "ANALYST")
    private Role role;
}