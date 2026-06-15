package com.zorvyn.assignment.financechatbot.config;

import com.zorvyn.assignment.financechatbot.security.CurrentToken;
import com.zorvyn.assignment.financechatbot.security.JwtService;
import com.zorvyn.assignment.financechatbot.security.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates the incoming bearer token (shared secret with the Finance API),
 * populates the Spring Security context with the user's role, and stashes the
 * raw token in the request-scoped {@link CurrentToken} so tools can forward it
 * downstream. Invalid tokens are left unauthenticated and rejected later by
 * the authorization rules in {@code SecurityConfig}.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CurrentToken currentToken;

    public JwtAuthFilter(JwtService jwtService, CurrentToken currentToken) {
        this.jwtService = jwtService;
        this.currentToken = currentToken;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String jwt = header.substring(BEARER_PREFIX.length());
            try {
                Claims claims = jwtService.parse(jwt);
                String username = jwtService.username(claims);
                String authorities = jwtService.authorities(claims);

                var auth = new UsernamePasswordAuthenticationToken(
                        username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(auth);

                currentToken.setToken(jwt);
                currentToken.setUsername(username);
                currentToken.setRole(Role.from(authorities));
            } catch (Exception ex) {
                // Invalid/expired token -> leave context unauthenticated.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
