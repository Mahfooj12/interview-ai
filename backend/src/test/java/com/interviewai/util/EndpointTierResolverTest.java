package com.interviewai.util;

import com.interviewai.dto.ratelimit.RateLimitTier;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class EndpointTierResolverTest {

    private final EndpointTierResolver resolver = new EndpointTierResolver();

    @Test
    void answer_is_ai_heavy() {
        var req = new MockHttpServletRequest("POST", "/api/interviews/abc/answer");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.AI_HEAVY);
    }

    @Test
    void report_generate_is_ai_heavy() {
        var req = new MockHttpServletRequest("POST", "/api/reports/generate/abc");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.AI_HEAVY);
    }

    @Test
    void start_interview_is_ai_normal() {
        var req = new MockHttpServletRequest("POST", "/api/interviews/abc/start");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.AI_NORMAL);
    }

    @Test
    void login_is_auth() {
        var req = new MockHttpServletRequest("POST", "/api/auth/login");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.AUTH);
    }

    @Test
    void health_is_exempt() {
        var req = new MockHttpServletRequest("GET", "/actuator/health");
        assertThat(resolver.resolve(req)).isNull();
    }

    @Test
    void swagger_is_exempt() {
        var req = new MockHttpServletRequest("GET", "/swagger-ui/index.html");
        assertThat(resolver.resolve(req)).isNull();
    }

    @Test
    void get_interviews_is_global() {
        var req = new MockHttpServletRequest("GET", "/api/interviews");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.GLOBAL);
    }

    @Test
    void resume_upload_is_upload() {
        var req = new MockHttpServletRequest("POST", "/api/resumes");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.UPLOAD);
    }

    @Test
    void voice_is_voice() {
        var req = new MockHttpServletRequest("POST", "/api/voice/transcribe");
        assertThat(resolver.resolve(req)).isEqualTo(RateLimitTier.VOICE);
    }
}