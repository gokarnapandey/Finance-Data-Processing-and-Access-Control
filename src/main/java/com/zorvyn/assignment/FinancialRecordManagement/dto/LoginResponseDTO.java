package com.zorvyn.assignment.FinancialRecordManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(name = "LoginResponseDTO", description = "Authentication results containing the session token and user details")
public class LoginResponseDTO {

    @Schema(description = "The email of the successfully authenticated user", example = "system@admin.com")
    private String email;

    @Schema(description = "The status message of the authentication request", example = "Success")
    private String status;

    @Schema(
            description = "The JWT (JSON Web Token) generated for the session. " +
                    "This must be included in the 'Authorization' header as 'Bearer <token>' for all subsequent requests.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzeXN0ZW1AYWRtaW4uY29tIiwiaWF0IjoxNzEyMTU0MDUyfQ..."
    )
    private String jwtToken;
}