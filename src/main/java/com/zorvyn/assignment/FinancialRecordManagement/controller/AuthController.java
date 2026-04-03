package com.zorvyn.assignment.FinancialRecordManagement.controller;

import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginRequestDto;
import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Public endpoints for user identity verification. " +
                "Provides JWT tokens for secure access to the Financial System."
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User Login",
            description = "Authenticates user credentials and returns a JWT (JSON Web Token). " +
                    "The resulting token must be used in the 'Authorization' header as a 'Bearer' token for all protected requests."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDto loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}