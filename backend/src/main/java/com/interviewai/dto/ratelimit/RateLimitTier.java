package com.interviewai.dto.ratelimit;

/**
 * Rate limit tiers. Each tier has its own capacity and refill rate,
 * configured in application.yml.
 */
public enum RateLimitTier {
    AUTH,
    AI_HEAVY,
    AI_NORMAL,
    UPLOAD,
    VOICE,
    GLOBAL;

    /** Tier that never rate-limits — used for health, swagger, OPTIONS. */
    public static final RateLimitTier EXEMPT = null;
}