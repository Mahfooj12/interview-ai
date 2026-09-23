package com.interviewai.ai.speech.impl;

import com.interviewai.ai.speech.SpeechToTextClient;
import com.interviewai.ai.speech.dto.SttRequest;
import com.interviewai.ai.speech.dto.SttResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Fallback STT client.
 * Activates ONLY when app.speech.provider is "none" (or the property is missing).
 * When SPEECH_PROVIDER is set to deepgram/openai, the real client takes over.
 */
@Component
@ConditionalOnProperty(
        name = "app.speech.provider",
        havingValue = "none",
        matchIfMissing = true
)
public class NoOpSttClient implements SpeechToTextClient {

    private static final Logger log = LoggerFactory.getLogger(NoOpSttClient.class);

    public NoOpSttClient() {
        log.warn("No speech-to-text provider configured - using NoOpSttClient. "
                + "Set SPEECH_PROVIDER to enable real transcription.");
    }

    @Override
    public SttResponse transcribe(SttRequest request) {
        return SttResponse.builder()
                .transcript("")
                .durationMs(0L)
                .confidence(null)
                .build();
    }

    @Override
    public String provider() {
        return "none";
    }
}
