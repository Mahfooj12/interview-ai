package com.interviewai.service.impl;

import com.interviewai.dto.ratelimit.RateLimitDecision;
import com.interviewai.dto.ratelimit.RateLimitTier;
import com.interviewai.service.RateLimitService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Fallback when Redis is disabled. Always allows the request.
 *
 * Activates when cache.redis.enabled is false OR missing.
 * The Redis implementation activates when the property is true,
 * so exactly one of the two is present at all times.
 */
@Service
@ConditionalOnProperty(
        name = "cache.redis.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class NoOpRateLimitService implements RateLimitService {

    @Override
    public RateLimitDecision check(String identity, RateLimitTier tier) {
        return RateLimitDecision.allow(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}