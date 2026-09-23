package com.interviewai.ai.speech.impl;

//import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.speech.TextToSpeechClient;
import com.interviewai.ai.speech.dto.TtsRequest;
import com.interviewai.ai.speech.dto.TtsResponse;
import com.interviewai.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.speech.provider", havingValue = "openai")
public class OpenAiTtsClient implements TextToSpeechClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiTtsClient.class);

    private final RestClient client;
    //private final ObjectMapper objectMapper;
    private final String model;

    public OpenAiTtsClient(
            RestClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${app.speech.openai.api-key:}") String apiKey,
            @Value("${app.speech.openai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${app.speech.openai.model:tts-1}") String model) {
        //this.objectMapper = objectMapper;
        this.model = model;
        this.client = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    public TtsResponse synthesize(TtsRequest request) {
        if (request.getText() == null || request.getText().isBlank()) {
            throw new BadRequestException("Text is required for synthesis");
        }
        long start = System.currentTimeMillis();
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", model);
            payload.put("voice", request.getVoice() == null ? "alloy" : request.getVoice());
            payload.put("input", request.getText());
            payload.put("format", "mp3");

            byte[] audio = client.post()
                    .uri("/v1/audio/speech")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(byte[].class);

            if (audio == null || audio.length == 0) {
                throw new BadRequestException("TTS provider returned empty audio");
            }
            long duration = System.currentTimeMillis() - start;
            log.debug("OpenAI TTS ok in {} ms ({} bytes)", duration, audio.length);
            return TtsResponse.builder()
                    .audio(audio)
                    .mimeType("audio/mpeg")
                    .durationMs(duration)
                    .build();
        } catch (Exception e) {
            log.error("OpenAI TTS failed: {}", e.getMessage(), e);
            throw new BadRequestException("Text-to-speech failed: " + e.getMessage());
        }
    }

    @Override
    public String provider() {
        return "openai";
    }
}