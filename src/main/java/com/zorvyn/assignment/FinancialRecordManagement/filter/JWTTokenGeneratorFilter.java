package com.zorvyn.assignment.FinancialRecordManagement.filter;

import com.zorvyn.assignment.FinancialRecordManagement.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(null !=  auth){
            Environment env = getEnvironment();
            if(null != env){
                String secret = env.getProperty(SecurityConstants.JWT_SECRET_KEY, SecurityConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                String jwt = Jwts.builder().setIssuer("ZORVYN_ASSESSMENT").setSubject(auth.getName())
                        .claim("username", auth.getName())
                        .claim("authorities", auth.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority
                        ).collect(Collectors.joining(",")))
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(new Date().getTime() + 300000000))
                        .signWith(secretKey)
                        .compact();

                response.setHeader(SecurityConstants.JWT_HEADER, jwt);
            }
        }

        filterChain.doFilter(request, response);
    }


    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/v1/login");
    }
}
