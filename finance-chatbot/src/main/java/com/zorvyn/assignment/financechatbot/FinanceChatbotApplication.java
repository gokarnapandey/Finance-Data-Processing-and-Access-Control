package com.zorvyn.assignment.financechatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring AI role-based chatbot that fronts the
 * Finance Record Management API with natural-language tool calling.
 */
@SpringBootApplication
public class FinanceChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceChatbotApplication.class, args);
    }
}
