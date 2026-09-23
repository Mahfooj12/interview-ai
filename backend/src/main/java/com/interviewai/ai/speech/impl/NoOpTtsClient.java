package com.interviewai.ai.speech.impl;

import com.interviewai.ai.speech.TextToSpeechClient;
import com.interviewai.ai.speech.dto.TtsRequest;
import com.interviewai.ai.speech.dto.TtsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Fallback TTS client.
 * Activates ONLY when app.speech.provider is "none" (or the property is missing).
 */
@Component
@ConditionalOnProperty(
        name = "app.speech.provider",
        havingValue = "none",
        matchIfMissing = true
)
public class NoOpTtsClient implements TextToSpeechClient {

    private static final Logger log = LoggerFactory.getLogger(NoOpTtsClient.class);

    public NoOpTtsClient() {
        log.warn("No text-to-speech provider configured - using NoOpTtsClient. "
                + "Set SPEECH_PROVIDER to enable real synthesis.");
    }

    @Override
    public TtsResponse synthesize(TtsRequest request) {
        return TtsResponse.builder()
                .audio(new byte[0])
                .mimeType("audio/mpeg")
                .durationMs(0L)
                .build();
    }

    @Override
    public String provider() {
        return "none";
    }
}
