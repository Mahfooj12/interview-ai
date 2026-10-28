package com.interviewai.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Random;

/**
 * Redis cache configuration for InterviewAI.
 *
 * Design notes:
 * - Cache-aside: application decides what/when to cache.
 * - JSON serialization for human-readable values and language-agnostic safety.
 * - Per-cache TTLs with randomized jitter to avoid cache stampedes.
 * - Custom CacheErrorHandler logs and swallows Redis errors, so the app
 *   continues to work when Redis is down (falls through to MongoDB).
 * - Enabled only when cache.redis.enabled=true.
 */
@Configuration
@ConditionalOnProperty(name = "cache.redis.enabled", havingValue = "true")
public class RedisConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    /** Jitter in seconds added to every TTL to spread cache expiry. */
    private static final int TTL_JITTER_SECONDS = 30;

    private final Random jitter = new Random();

    @Value("${cache.redis.progress-ttl-seconds:120}")
    private long progressTtlSeconds;

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration(ObjectMapper baseObjectMapper) {
        // Dedicated ObjectMapper for cache values: we don't want the cache to
        // interfere with HTTP serialization settings.
        ObjectMapper mapper = baseObjectMapper.copy();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(progressTtlSeconds + jitter.nextInt(TTL_JITTER_SECONDS)))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(valueSerializer))
                .prefixCacheNameWith("interviewai:");
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration defaultConfig) {

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("progress",
                        defaultConfig.entryTtl(Duration.ofSeconds(
                                progressTtlSeconds + jitter.nextInt(TTL_JITTER_SECONDS))))
                .build();
    }

    /**
     * When Redis is unavailable, don't fail requests — log at WARN and
     * continue to the fallback (MongoDB).
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis GET failed for cache={} key={}: {}", cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("Redis PUT failed for cache={} key={}: {}", cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis EVICT failed for cache={} key={}: {}", cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                log.warn("Redis CLEAR failed for cache={}: {}", cache.getName(), exception.getMessage());
            }
        };
    }
}