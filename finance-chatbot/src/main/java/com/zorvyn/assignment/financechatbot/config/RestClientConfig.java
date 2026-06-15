package com.zorvyn.assignment.financechatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RestClient pointed at the downstream Finance Record Management API.
 * The bearer token is attached per-request inside {@code FinanceApiClient}.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient financeRestClient(RestClient.Builder builder,
                                        @Value("${finance.api.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
