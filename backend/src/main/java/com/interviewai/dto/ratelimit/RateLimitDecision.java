package com.interviewai.dto.ratelimit;

/**
 * Result of a rate-limit check.
 *
 * @param allowed         true if the request may proceed
 * @param limit           capacity of the tier
 * @param remaining       tokens remaining after this request (0 if rejected)
 * @param retryAfterSecs  seconds until next token; 0 if allowed
 */
public record RateLimitDecision(
        boolean allowed,
        int limit,
        int remaining,
        long retryAfterSecs
) {
    public static RateLimitDecision allow(int limit, int remaining) {
        return new RateLimitDecision(true, limit, remaining, 0);
    }

    public static RateLimitDecision deny(int limit, long retryAfterSecs) {
        return new RateLimitDecision(false, limit, 0, retryAfterSecs);
    }
}
