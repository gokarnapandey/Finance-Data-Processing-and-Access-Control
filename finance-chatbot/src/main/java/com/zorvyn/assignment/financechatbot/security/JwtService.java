package com.zorvyn.assignment.financechatbot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Parses and validates the JWTs issued by the Finance API. Uses the same
 * HMAC secret and the same {@code username}/{@code authorities} claims, so a
 * token minted by the Finance app's /api/v1/login validates here unchanged.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Parses the signed token and returns its claims, throwing if invalid/expired. */
    public Claims parse(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String username(Claims claims) {
        return String.valueOf(claims.get("username"));
    }

    /** Comma-separated authority string as written by the Finance API (e.g. "ADMIN"). */
    public String authorities(Claims claims) {
        return String.valueOf(claims.get("authorities"));
    }
}
