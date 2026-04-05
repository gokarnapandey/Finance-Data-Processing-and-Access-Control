package com.zorvyn.assignment.FinancialRecordManagement.rateLimit;

public class Bucket {

    private int tokens;
    private long lastRefillTime;
    private final int capacity;
    private final long refillIntervalNs; // 1. Change type to long

    // 2. Update constructor to accept long
    public Bucket(int capacity, long refillIntervalNs) {
        this.capacity = capacity;
        this.refillIntervalNs = refillIntervalNs;
        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public synchronized boolean tryConsume() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedTime = now - lastRefillTime;

        // 3. Use the internal field instead of the static constant directly
        // This makes the Bucket more reusable if you change rates later
        long intervalsPassed = elapsedTime / refillIntervalNs;

        if (intervalsPassed > 0) {
            int tokensToAdd = (int) (intervalsPassed * RateLimitConstants.REFILL_AMOUNT);
            tokens = Math.min(capacity, tokens + tokensToAdd);

            // Move the clock forward by the intervals actually "spent"
            lastRefillTime += intervalsPassed * refillIntervalNs;
        }
    }
}