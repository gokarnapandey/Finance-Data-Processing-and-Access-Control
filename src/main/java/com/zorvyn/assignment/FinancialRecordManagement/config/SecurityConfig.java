package com.zorvyn.assignment.FinancialRecordManagement.config;

import com.zorvyn.assignment.FinancialRecordManagement.exception.CustomAccessDeniedHandler;
import com.zorvyn.assignment.FinancialRecordManagement.exception.CustomBasicAuthenticationEntryPoint;
import com.zorvyn.assignment.FinancialRecordManagement.filter.JWTTokenGeneratorFilter;
import com.zorvyn.assignment.FinancialRecordManagement.filter.JWTTokenValidatorFilter;
import com.zorvyn.assignment.FinancialRecordManagement.rateLimit.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RateLimitFilter rateLimitFilter) throws Exception{

        http.sessionManagement(
                smc -> smc.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, JWTTokenValidatorFilter.class)


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/dashboard").hasAnyAuthority("ADMIN", "ANALYST", "VIEWER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/records/**").hasAnyAuthority("ADMIN", "ANALYST")
                        .requestMatchers(HttpMethod.POST, "/api/v1/records/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/records/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/records/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/users").hasAuthority("ADMIN")
                        .anyRequest().authenticated())

                 .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))

                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // Supports multiple encoding algorithms
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
