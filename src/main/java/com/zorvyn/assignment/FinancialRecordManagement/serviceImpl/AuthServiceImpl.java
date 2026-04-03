package com.zorvyn.assignment.FinancialRecordManagement.serviceImpl;

import com.zorvyn.assignment.FinancialRecordManagement.constants.SecurityConstants;
import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginRequestDto;
import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Environment env;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponseDTO login(LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        String jwt = generateJwtToken(authentication);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setEmail(authentication.getName());
        response.setStatus("SUCCESS");
        response.setJwtToken(jwt);

        return response;
    }

    private String generateJwtToken(Authentication authentication) {
        String secret = env.getProperty(SecurityConstants.JWT_SECRET_KEY,
                SecurityConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setIssuer("ZORVYN_ASSESSMENT")
                .setSubject(authentication.getName())
                .claim("username", authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 30000000))
                .signWith(key)
                .compact();
    }
}