package com.zorvyn.assignment.FinancialRecordManagement.serviceImplTest;


import com.zorvyn.assignment.FinancialRecordManagement.constants.SecurityConstants;
import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginRequestDto;
import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.serviceImpl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private Environment env;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequestDto loginRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@zorvyn.com");
        loginRequest.setPassword("password123");
        authentication = mock(Authentication.class);
    }

    @Test
    void login_Success_ReturnsValidResponse() {
        String mockSecret = "very-long-secret-key-that-is-at-least-256-bits-long";
        when(env.getProperty(eq(SecurityConstants.JWT_SECRET_KEY), anyString()))
                .thenReturn(mockSecret);

        when(authentication.getName()).thenReturn("test@zorvyn.com");
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        LoginResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("test@zorvyn.com", response.getEmail());
        assertNotNull(response.getJwtToken());

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad Credentials"));

        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any());
        verify(env, never()).getProperty(anyString(), anyString());
    }
}
