package com.interviewai.service.impl;

import com.interviewai.config.RateLimitConfig;
import com.interviewai.dto.ratelimit.RateLimitDecision;
import com.interviewai.dto.ratelimit.RateLimitTier;
import com.interviewai.service.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnProperty(name = "cache.redis.enabled", havingValue = "true")
public class RedisRateLimitService implements RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RedisRateLimitService.class);

    /**
     * Atomic token bucket. Returns [allowed (0|1), remaining tokens].
     */
    private static final String LUA_SCRIPT = """
            local capacity = tonumber(ARGV[1])
            local refill_rate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local requested = tonumber(ARGV[4])

            local bucket = redis.call('HMGET', KEYS[1], 'tokens', 'last_refill')
            local tokens = tonumber(bucket[1])
            local last_refill = tonumber(bucket[2])
            if tokens == nil then tokens = capacity end
            if last_refill == nil then last_refill = now end

            local elapsed = math.max(0, now - last_refill) / 1000.0
            tokens = math.min(capacity, tokens + elapsed * refill_rate)

            local allowed = 0
            if tokens >= requested then
                tokens = tokens - requested
                allowed = 1
            end

            redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last_refill', now)
            local ttl = math.ceil(capacity / refill_rate) + 60
            redis.call('EXPIRE', KEYS[1], ttl)

            return {allowed, math.floor(tokens)}
            """;

    private final StringRedisTemplate redis;
    private final RateLimitConfig config;

    @SuppressWarnings("rawtypes")
    private final RedisScript<List> script;

    public RedisRateLimitService(StringRedisTemplate redis, RateLimitConfig config) {
        this.redis = redis;
        this.config = config;

        @SuppressWarnings("rawtypes")
        DefaultRedisScript<List> s = new DefaultRedisScript<>();
        s.setScriptText(LUA_SCRIPT);
        s.setResultType(List.class);
        this.script = s;
    }

    @Override
    public RateLimitDecision check(String identity, RateLimitTier tier) {
        if (!config.isEnabled()) {
            return RateLimitDecision.allow(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        RateLimitConfig.TierConfig tc = config.getTiers().get(tier);
        if (tc == null || tc.getCapacity() <= 0) {
            // Tier not configured → no limit
            return RateLimitDecision.allow(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        String key = "ratelimit:" + tier.name() + ":" + identity;
        long nowMs = System.currentTimeMillis();
        double refillRate = tc.refillRatePerSecond();

        try {
            @SuppressWarnings("unchecked")
            List<Long> result = (List<Long>) redis.execute(
                    script,
                    Collections.singletonList(key),
                    String.valueOf(tc.getCapacity()),
                    String.valueOf(refillRate),
                    String.valueOf(nowMs),
                    "1"
            );

            if (result == null || result.size() < 2) {
                log.warn("Rate limit script returned unexpected result: {}", result);
                return RateLimitDecision.allow(tc.getCapacity(), tc.getCapacity());
            }

            boolean allowed = result.get(0) == 1L;
            int remaining = result.get(1).intValue();

            if (allowed) {
                return RateLimitDecision.allow(tc.getCapacity(), remaining);
            } else {
                long retryAfter = refillRate > 0
                        ? (long) Math.ceil(1.0 / refillRate)
                        : tc.getFullRefillSeconds();
                return RateLimitDecision.deny(tc.getCapacity(), retryAfter);
            }

        } catch (Exception e) {
            // Fail open: Redis is unavailable, allow the request.
            log.warn("Rate limit check failed for tier={} identity={} — allowing request: {}",
                    tier, identity, e.getMessage());
            return RateLimitDecision.allow(tc.getCapacity(), tc.getCapacity());
        }
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }
}