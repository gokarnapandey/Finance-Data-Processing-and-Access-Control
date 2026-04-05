package com.zorvyn.assignment.FinancialRecordManagement.rateLimit;

public class RateLimitConstants {
    public static final int MAX_TOKENS = 5;
    // Change to 10 seconds per token for testing
    public static final long REFILL_INTERVAL_NS = 10_000_000_000L;
    public static final int REFILL_AMOUNT = 1;
}
