package com.zorvyn.assignment.financechatbot.controller;

import com.zorvyn.assignment.financechatbot.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Convenience proxy so the chatbot is a single entry point for demos: it forwards
 * credentials to the Finance API's /api/v1/login and relays the JWT response.
 * Clients may also log in against the Finance API directly and skip this.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthProxyController {

    private final RestClient financeRestClient;

    public AuthProxyController(RestClient financeRestClient) {
        this.financeRestClient = financeRestClient;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        try {
            String body = financeRestClient.post()
                    .uri("/api/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(body);
        } catch (RestClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }
}
