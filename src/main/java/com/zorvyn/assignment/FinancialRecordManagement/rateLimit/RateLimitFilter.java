package com.zorvyn.assignment.FinancialRecordManagement.rateLimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final KeyResolver keyResolver;

    public RateLimitFilter(RateLimiterService rateLimiterService,
                           KeyResolver keyResolver) {
        this.rateLimiterService = rateLimiterService;
        this.keyResolver = keyResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String key = keyResolver.resolve(request);

        boolean allowed = rateLimiterService.isAllowed(key);

        if (!allowed) {
            response.setStatus(429);
            response.setContentType("application/json");

            response.setHeader("Retry-After", "10");
            response.setHeader("X-RateLimit-Limit", "5");
            response.setHeader("X-RateLimit-Remaining", "0");

            String body = """
    {
      "timestamp": "%s",
      "status": 429,
      "error": "Too Many Requests",
      "message": "Rate limit exceeded. Please try again later.",
      "path": "%s"
    }
    """.formatted(
                    java.time.Instant.now(),
                    request.getRequestURI()
            );

            response.getWriter().write(body);
            response.flushBuffer();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
