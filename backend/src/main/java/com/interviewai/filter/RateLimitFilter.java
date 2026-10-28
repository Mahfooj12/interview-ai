package com.interviewai.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.dto.ratelimit.RateLimitDecision;
import com.interviewai.dto.ratelimit.RateLimitTier;
import com.interviewai.exception.ApiError;
import com.interviewai.service.RateLimitService;
import com.interviewai.util.EndpointTierResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Applies rate limits to requests based on endpoint and user identity.
 *
 * Positioned AFTER CORS (so preflight never blocks) and BEFORE JWT auth
 * (so abusive requests are rejected before expensive work happens).
 */
@Component
@Order(10)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimitService rateLimitService;
    private final EndpointTierResolver tierResolver;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RateLimitService rateLimitService,
                           EndpointTierResolver tierResolver,
                           ObjectMapper objectMapper) {
        this.rateLimitService = rateLimitService;
        this.tierResolver = tierResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        // Never rate-limit preflight requests.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        RateLimitTier tier = tierResolver.resolve(request);
        if (tier == null) {
            // Exempt endpoint (health, swagger, etc.)
            chain.doFilter(request, response);
            return;
        }

        if (!rateLimitService.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        String identity = resolveIdentity(request);
        RateLimitDecision decision = rateLimitService.check(identity, tier);

        // Always send limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(decision.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(decision.remaining()));

        if (!decision.allowed()) {
            response.setHeader("Retry-After", String.valueOf(decision.retryAfterSecs()));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiError body = ApiError.of(
                    "Rate limit exceeded. Try again in " + decision.retryAfterSecs() + " seconds.",
                    "RATE_LIMIT_EXCEEDED",
                    request.getRequestURI());
            objectMapper.writeValue(response.getOutputStream(), body);

            log.info("Rate limit exceeded — tier={} identity={} path={}",
                    tier, identity, request.getRequestURI());
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Extract identity: prefer authenticated user ID from JWT subject,
     * fall back to forwarded IP.
     */
    private String resolveIdentity(HttpServletRequest request) {
        // Cheap: read uid claim from JWT without full verification.
        // Filter runs before JwtAuthenticationFilter, so SecurityContext isn't populated.
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String userId = extractUserIdUnverified(token);
                if (userId != null) return "user:" + userId;
            } catch (Exception ignored) {
                // fall through to IP
            }
        }
        return "ip:" + clientIp(request);
    }

    /**
     * Minimal JWT payload extraction. Does NOT verify signature —
     * that happens later in JwtAuthenticationFilter. We only need the uid
     * to key the rate limiter. If someone spoofs the uid, they can only
     * limit someone else's bucket, not bypass their own.
     */
    private String extractUserIdUnverified(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        // Hack-free: use Jackson
        try {
            var node = objectMapper.readTree(payload);
            return node.has("uid") ? node.get("uid").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String clientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
