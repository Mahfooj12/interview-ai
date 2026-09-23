package com.interviewai.ai.speech.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.speech.SpeechToTextClient;
import com.interviewai.ai.speech.dto.SttRequest;
import com.interviewai.ai.speech.dto.SttResponse;
import com.interviewai.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.speech.provider", havingValue = "deepgram")
public class DeepgramSttClient implements SpeechToTextClient {

    private static final Logger log = LoggerFactory.getLogger(DeepgramSttClient.class);

    private final RestClient client;
    private final ObjectMapper objectMapper;

    public DeepgramSttClient(
            RestClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${app.speech.deepgram.api-key:}") String apiKey,
            @Value("${app.speech.deepgram.base-url:https://api.deepgram.com}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.client = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Token " + apiKey)
                .build();
    }

    @Override
    public SttResponse transcribe(SttRequest request) {
        if (request.getAudio() == null || request.getAudio().length == 0) {
            throw new BadRequestException("Empty audio payload");
        }
        long start = System.currentTimeMillis();
        try {
            String query = "/v1/listen?smart_format=true&punctuate=true&language="
                    + (request.getLanguage() == null ? "en" : request.getLanguage());

            String raw = client.post()
                    .uri(query)
                    .contentType(MediaType.parseMediaType(
                            request.getMimeType() == null ? "audio/webm" : request.getMimeType()))
                    .body(request.getAudio())
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(raw);
            JsonNode alternatives = root.path("results")
                    .path("channels").path(0)
                    .path("alternatives");
            String transcript = "";
            double confidence = 0;
            if (alternatives.isArray() && !alternatives.isEmpty()) {
                JsonNode alt = alternatives.get(0);
                transcript = alt.path("transcript").asText("");
                confidence = alt.path("confidence").asDouble(0);
            }
            long duration = System.currentTimeMillis() - start;
            log.debug("Deepgram STT ok in {} ms", duration);
            return SttResponse.builder()
                    .transcript(transcript)
                    .durationMs(duration)
                    .confidence(confidence)
                    .build();
        } catch (Exception e) {
            log.error("Deepgram STT failed: {}", e.getMessage(), e);
            throw new BadRequestException("Speech-to-text failed: " + e.getMessage());
        }
    }

    @Override
    public String provider() {
        return "deepgram";
    }
}