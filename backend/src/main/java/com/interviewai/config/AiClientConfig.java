package com.interviewai.config;

import com.interviewai.ai.client.AiClient;
import com.interviewai.ai.client.DeepSeekClient;
import com.interviewai.ai.client.GeminiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Picks the active {@link AiClient} based on the {@code AI_PROVIDER} env var.
 *
 *   AI_PROVIDER=deepseek  → DeepSeekClient (default)
 *   AI_PROVIDER=gemini    → GeminiClient
 */
@Configuration
public class AiClientConfig {

    private static final Logger log = LoggerFactory.getLogger(AiClientConfig.class);

    @Bean
    @Primary
    public AiClient activeAiClient(
            DeepSeekClient deepSeekClient,
            GeminiClient geminiClient,
            @Value("${app.ai.provider:deepseek}") String provider) {

        String p = provider == null ? "deepseek" : provider.trim().toLowerCase();
        switch (p) {
            case "gemini":
                log.info("Active AI provider: GEMINI (model={})", "configured via app.ai.gemini.model");
                return geminiClient;
            case "deepseek":
            default:
                log.info("Active AI provider: DEEPSEEK");
                return deepSeekClient;
        }
    }
}