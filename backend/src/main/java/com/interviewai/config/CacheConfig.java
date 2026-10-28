package com.interviewai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's caching abstraction.
 * Only active when cache.redis.enabled=true.
 *
 * When disabled (e.g., in tests), @Cacheable / @CacheEvict annotations are
 * no-ops — the method body runs every time, hitting MongoDB directly.
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "cache.redis.enabled", havingValue = "true")
public class CacheConfig {
}
