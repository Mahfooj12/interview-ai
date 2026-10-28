package com.interviewai.util;

import com.interviewai.dto.ratelimit.RateLimitTier;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Maps HTTP method + path to a rate limit tier.
 *
 * Returns null for exempt endpoints (health, swagger, static).
 */
@Component
public class EndpointTierResolver {

    // Regex patterns for endpoints with path params.
    private static final Pattern AI_HEAVY = Pattern.compile(
            "^/api/interviews/[^/]+/(answer|complete)$|^/api/reports/generate/[^/]+$"
    );
    private static final Pattern AI_NORMAL = Pattern.compile(
            "^/api/resumes/[^/]+/analyze$" +
            "|^/api/jobs/[^/]+/match$" +
            "|^/api/interviews/[^/]+/(start|next-question)$"
    );
    private static final Pattern UPLOAD_POST = Pattern.compile(
            "^/api/resumes$|^/api/jobs/upload$"
    );
    private static final Pattern AUTH = Pattern.compile(
            "^/api/auth/(register|login|refresh)$"
    );
    private static final Pattern VOICE = Pattern.compile(
            "^/api/voice/.*$"
    );
    private static final Pattern JD_CREATE = Pattern.compile("^/api/jobs$");

    public RateLimitTier resolve(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Exempt endpoints
        if (path.startsWith("/actuator/") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.equals("/actuator/health")) {
            return null;
        }

        // Only apply rate limit to /api/**
        if (!path.startsWith("/api/")) {
            return null;
        }

        // AI_HEAVY
        if (AI_HEAVY.matcher(path).matches()) {
            return RateLimitTier.AI_HEAVY;
        }

        // AI_NORMAL
        if (AI_NORMAL.matcher(path).matches()) {
            return RateLimitTier.AI_NORMAL;
        }

        // POST /api/jobs (create JD text → AI parse)
        if ("POST".equalsIgnoreCase(method) && JD_CREATE.matcher(path).matches()) {
            return RateLimitTier.AI_NORMAL;
        }

        // UPLOAD
        if ("POST".equalsIgnoreCase(method) && UPLOAD_POST.matcher(path).matches()) {
            return RateLimitTier.UPLOAD;
        }

        // AUTH
        if (AUTH.matcher(path).matches()) {
            return RateLimitTier.AUTH;
        }

        // VOICE
        if (VOICE.matcher(path).matches()) {
            return RateLimitTier.VOICE;
        }

        // Everything else under /api/**
        return RateLimitTier.GLOBAL;
    }
}
