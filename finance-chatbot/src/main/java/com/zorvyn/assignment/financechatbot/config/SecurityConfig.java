package com.zorvyn.assignment.financechatbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorvyn.assignment.financechatbot.security.CurrentToken;
import com.zorvyn.assignment.financechatbot.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Stateless security for the chatbot. The /api/v1/chat endpoint requires a valid
 * Finance API JWT; the login proxy and health endpoint are public. Authorization
 * within the chat (which tools a role may use) is enforced by the ToolRegistry,
 * and re-enforced downstream by the Finance API itself.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   CurrentToken currentToken,
                                                   ObjectMapper objectMapper) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthFilter(jwtService, currentToken),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->
                        writeUnauthorized(response, objectMapper, request.getServletPath())));

        return http.build();
    }

    private void writeUnauthorized(HttpServletResponse response, ObjectMapper objectMapper, String path)
            throws java.io.IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 401,
                "error", "Unauthorized",
                "message", "A valid Finance API bearer token is required",
                "path", path));
    }
}
