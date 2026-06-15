package com.zorvyn.assignment.financechatbot.tools;

import com.zorvyn.assignment.financechatbot.client.FinanceApiClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User-management tools. Available to ADMIN only (also re-enforced by the Finance API).
 */
@Component
public class UserAdminTools {

    private final FinanceApiClient finance;

    public UserAdminTools(FinanceApiClient finance) {
        this.finance = finance;
    }

    @Tool(description = "Create a new user account. Returns the created user as JSON. "
            + "Passwords must be 8-20 chars with upper, lower, digit and a special character.")
    public String createUser(
            @ToolParam(description = "Full name (2-50 chars).") String name,
            @ToolParam(description = "Unique email address.") String email,
            @ToolParam(description = "10-digit mobile number.") String mobileNumber,
            @ToolParam(description = "Password (8-20 chars, mixed case, digit, special char).") String password,
            @ToolParam(description = "Role. One of: VIEWER, ANALYST, ADMIN.") String role) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("mobileNumber", mobileNumber);
        body.put("password", password);
        body.put("role", role);
        body.put("status", true);
        body.put("isDeleted", false);
        return finance.createUser(body);
    }

    @Tool(description = "List all active (non-deleted) users as JSON.")
    public String listUsers() {
        return finance.listUsers();
    }

    @Tool(description = "Get a single user by their business user id as JSON.")
    public String getUserById(
            @ToolParam(description = "The user's business id (userId).") String userId) {
        return finance.getUser(userId);
    }

    @Tool(description = "Update a user's profile. Only the provided fields change; email cannot be changed. "
            + "Returns the updated user as JSON.")
    public String updateUser(
            @ToolParam(description = "The user's business id (userId).") String userId,
            @ToolParam(required = false, description = "New name (2-50 chars).") String name,
            @ToolParam(required = false, description = "New 10-digit mobile number.") String mobileNumber,
            @ToolParam(required = false, description = "New password (8-20 chars, complexity rules).") String password,
            @ToolParam(required = false, description = "New role: VIEWER, ANALYST, ADMIN.") String role,
            @ToolParam(required = false, description = "Active status true/false.") Boolean status) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null) body.put("name", name);
        if (mobileNumber != null) body.put("mobileNumber", mobileNumber);
        if (password != null) body.put("password", password);
        if (role != null) body.put("role", role);
        if (status != null) body.put("status", status);
        return finance.updateUser(userId, body);
    }

    @Tool(description = "Enable or disable a user's login by toggling their active status. Returns JSON.")
    public String updateUserStatus(
            @ToolParam(description = "The user's business id (userId).") String userId,
            @ToolParam(description = "true to activate, false to deactivate.") Boolean active) {
        return finance.updateUserStatus(userId, Boolean.TRUE.equals(active));
    }

    @Tool(description = "Soft-delete a user by their business user id (login disabled, record retained). Returns JSON.")
    public String deleteUser(
            @ToolParam(description = "The user's business id (userId).") String userId) {
        return finance.deleteUser(userId);
    }

    @Tool(description = "List all soft-deleted users (the recycle bin) as JSON.")
    public String listDeletedUsers() {
        return finance.listDeletedUsers();
    }
}
