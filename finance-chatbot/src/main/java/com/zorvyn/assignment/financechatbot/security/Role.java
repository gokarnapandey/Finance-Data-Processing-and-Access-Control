package com.zorvyn.assignment.financechatbot.security;

/**
 * Roles mirrored from the Finance API. The JWT carries the role name in the
 * {@code authorities} claim (e.g. "ADMIN"). Anything unrecognised is treated
 * as the least-privileged VIEWER.
 */
public enum Role {
    VIEWER,
    ANALYST,
    ADMIN;

    public static Role from(String value) {
        if (value == null) {
            return VIEWER;
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return VIEWER;
        }
    }
}
