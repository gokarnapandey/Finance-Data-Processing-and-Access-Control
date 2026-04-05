package com.zorvyn.assignment.FinancialRecordManagement.rateLimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class KeyResolver {

    public String resolve(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        return ip + ":" + endpoint;
    }
}