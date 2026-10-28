package com.interviewai.config;

import com.interviewai.dto.ratelimit.RateLimitTier;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Externalized rate limit configuration.
 *
 * Example YAML:
 * <pre>
 * rate-limit:
 *   enabled: true
 *   tiers:
 *     AI_HEAVY:
 *       capacity: 15
 *       window-seconds: 3600
 *     AI_NORMAL:
 *       capacity: 60
 *       window-seconds: 3600
 * </pre>
 *
 * The refill rate is derived from capacity/window: capacity tokens per window.
 */
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitConfig {

    /** Master switch. When false, no rate limiting happens. */
    private boolean enabled = true;

    /** Per-tier configuration. */
    private Map<RateLimitTier, TierConfig> tiers = new EnumMap<>(RateLimitTier.class);

    /** IP address of the client if authenticated user is not found. */
    private boolean trustForwardedFor = true;

    @Getter
    @Setter
    public static class TierConfig {
        /** Maximum tokens in the bucket (= burst limit). */
        private int capacity;

        /** Window length in seconds. Refill rate = capacity / windowSeconds. */
        private long windowSeconds;

        /** Refill rate = capacity / windowSeconds (tokens per second). */
        public double refillRatePerSecond() {
            return windowSeconds > 0 ? (double) capacity / windowSeconds : 1.0;
        }

        /** Time in seconds to fully refill from empty. */
        public long getFullRefillSeconds() {
            return windowSeconds;
        }
    }
}
