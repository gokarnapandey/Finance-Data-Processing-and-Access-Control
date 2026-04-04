package com.zorvyn.assignment.FinancialRecordManagement.controller;

import com.zorvyn.assignment.FinancialRecordManagement.dto.UserRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserUpdateRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "User Management (Admin Only)",
        description = "Restricted endpoints for administrative user control. " +
                "Only users with 'ADMIN' authority can create, view, or modify user accounts."
)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Requirement 1 & 4: Registers a new user into the system. Accessible by Admin only. Make Sure Role should one of these 3 : VIEWER, ANALYST, ADMIN"
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "Role should be: . VIEWER, ANALYST, ADMIN")
            @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Operation(
            summary = "Fetch all active users",
            description = "Retrieves a list of all users who are currently active and not soft-deleted."
    )
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @Operation(
            summary = "Get user by ID",
            description = "Fetch detailed profile information for a specific user using their Unique ID."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }


    @Operation(
            summary = "Update user profile",
            description = "Allows updating existing user details like Name, Role, or Mobile Number."
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }


    @Operation(
            summary = "Toggle user active status",
            description = "Requirement 4: Updates the 'active' status of a user (Enable/Disable login access)."
    )
    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable String userId,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, active));
    }


    @Operation(
            summary = "Soft delete a user",
            description = "Requirement 5: Marks a user as deleted in the database without removing the record, preserving financial audit trails."
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "View deleted users (Recycle Bin)",
            description = "Administrative view to see all users currently marked as soft-deleted."
    )
    @GetMapping("/deleted")
    public ResponseEntity<List<UserResponseDTO>> getDeletedUsers() {
        return ResponseEntity.ok(userService.getAllDeletedUsers());
    }
}