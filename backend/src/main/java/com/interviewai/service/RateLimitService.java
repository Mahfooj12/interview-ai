package com.interviewai.service;

import com.interviewai.dto.ratelimit.RateLimitDecision;
import com.interviewai.dto.ratelimit.RateLimitTier;

public interface RateLimitService {

    /**
     * Check whether the given identity may proceed under the tier.
     *
     * @param identity  stable identifier (userId or "ip:1.2.3.4")
     * @param tier      which limit to apply
     */
    RateLimitDecision check(String identity, RateLimitTier tier);

    /**
     * Whether rate limiting is currently active.
     */
    boolean isEnabled();
}
