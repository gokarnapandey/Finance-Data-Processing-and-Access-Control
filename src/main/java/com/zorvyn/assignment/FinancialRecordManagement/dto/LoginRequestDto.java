package com.zorvyn.assignment.FinancialRecordManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "LoginRequestDto", description = "User credentials required for authentication and JWT generation")
public class LoginRequestDto {

    @Schema(
            description = "Registered email address of the user",
            example = "system@admin.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "User's account password",
            example = "Admin@12345",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password is required")
    private String password;
}