package com.zorvyn.assignment.FinancialRecordManagement.rateLimit;


import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        Bucket bucket = cache.computeIfAbsent(
                key,
                k -> new Bucket(
                        RateLimitConstants.MAX_TOKENS,
                         RateLimitConstants.REFILL_INTERVAL_NS // Use the Nanos constant
                )
        );

        return bucket.tryConsume();
    }
}
