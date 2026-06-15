package com.zorvyn.assignment.financechatbot.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request-scoped holder for the raw bearer token of the current call.
 * Populated by {@code JwtAuthFilter} and read by {@code FinanceApiClient} when
 * forwarding the call downstream. Tool execution runs on the request thread, so
 * the scope is active throughout the chat round-trip.
 */
@Component
@RequestScope
public class CurrentToken {

    private String token;
    private Role role = Role.VIEWER;
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
