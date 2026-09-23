package com.interviewai.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.client.dto.ChatChoice;
import com.interviewai.ai.client.dto.ChatCompletionResponse;
import com.interviewai.ai.client.dto.ChatMessage;
import com.interviewai.ai.client.dto.ChatUsage;
import com.interviewai.ai.client.gemini.dto.GeminiRequest;
import com.interviewai.ai.client.gemini.dto.GeminiResponse;
import com.interviewai.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Google Gemini implementation of {@link AiClient}.
 *
 * Notes:
 * - Gemini auth uses an API key query parameter, not a Bearer header.
 * - The system prompt is passed via {@code systemInstruction}.
 * - For JSON mode we set {@code responseMimeType=application/json}.
 * - The response shape is adapted back to the DeepSeek-compatible
 *   {@link ChatCompletionResponse} so callers do not need to change.
 */
@Component
public class GeminiClient implements AiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String model;
    private final String apiKey;

    public GeminiClient(
            RestClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${app.ai.gemini.api-key:}") String apiKey,
            @Value("${app.ai.gemini.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
            @Value("${app.ai.gemini.model:gemini-2.0-flash}") String model) {
        this.objectMapper = objectMapper;
        this.model = model;
        this.apiKey = apiKey;
        this.client = builder.baseUrl(baseUrl).build();
    }

    @Override
    public ChatCompletionResponse chatJson(List<ChatMessage> messages, double temperature, int maxTokens) {
        return execute(messages, temperature, maxTokens, true);
    }

    @Override
    public ChatCompletionResponse chatText(List<ChatMessage> messages, double temperature, int maxTokens) {
        return execute(messages, temperature, maxTokens, false);
    }

    @Override
    public String provider() {
        return "gemini";
    }

    private ChatCompletionResponse execute(
            List<ChatMessage> messages,
            double temperature,
            int maxTokens,
            boolean jsonMode) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException("GEMINI_API_KEY is not configured");
        }

        long start = System.currentTimeMillis();

        // Split system prompt from conversation turns
        String systemInstruction = null;
        List<GeminiRequest.Content> contents = new ArrayList<>();
        for (ChatMessage m : messages) {
            if ("system".equalsIgnoreCase(m.getRole())) {
                systemInstruction = m.getContent();
            } else {
                String role = "assistant".equalsIgnoreCase(m.getRole()) ? "model" : "user";
                contents.add(GeminiRequest.Content.builder()
                        .role(role)
                        .parts(List.of(GeminiRequest.Part.builder().text(m.getContent()).build()))
                        .build());
            }
        }

        GeminiRequest.GenerationConfig.GenerationConfigBuilder cfg = GeminiRequest.GenerationConfig.builder()
                .temperature(temperature)
                .maxOutputTokens(maxTokens)
                .topP(1.0);
        if (jsonMode) {
            cfg.responseMimeType("application/json");
        }

        GeminiRequest request = GeminiRequest.builder()
                .contents(contents)
                .systemInstruction(systemInstruction == null ? null :
                        GeminiRequest.Content.builder()
                                .parts(List.of(GeminiRequest.Part.builder().text(systemInstruction).build()))
                                .build())
                .generationConfig(cfg.build())
                .build();

        try {
            String uri = "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

            GeminiResponse response = client.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String body = new String(res.getBody().readAllBytes());
                        log.error("Gemini error status={} body={}", res.getStatusCode(), body);
                        throw new BadRequestException("AI provider error: " + res.getStatusCode());
                    })
                    .body(GeminiResponse.class);

            String content = response != null ? response.firstText() : null;
            if (content == null || content.isBlank()) {
                throw new BadRequestException("Gemini returned an empty response");
            }

            log.debug("Gemini call ok in {} ms (model={})", System.currentTimeMillis() - start, model);

            return toChatCompletionResponse(content, response);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini call failed: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to call AI provider: " + e.getMessage());
        }
    }

    /**
     * Adapt Gemini's response shape into the DeepSeek/OpenAI-compatible shape
     * that {@code AIServiceImpl} already understands.
     */
    private ChatCompletionResponse toChatCompletionResponse(String content, GeminiResponse gemini) {
        ChatMessage message = ChatMessage.builder()
                .role("assistant")
                .content(stripJsonFence(content))
                .build();
        ChatChoice choice = ChatChoice.builder()
                .index(0)
                .message(message)
                .finishReason(gemini.getCandidates() != null && !gemini.getCandidates().isEmpty()
                        ? gemini.getCandidates().get(0).getFinishReason() : "stop")
                .build();

        ChatUsage usage = ChatUsage.builder().build();
        if (gemini.getUsageMetadata() != null) {
            usage.setPromptTokens(gemini.getUsageMetadata().getPromptTokenCount());
            usage.setCompletionTokens(gemini.getUsageMetadata().getCandidatesTokenCount());
            usage.setTotalTokens(gemini.getUsageMetadata().getTotalTokenCount());
        }

        return ChatCompletionResponse.builder()
                .id("gemini-" + System.currentTimeMillis())
                .model(gemini.getModelVersion() != null ? gemini.getModelVersion() : model)
                .created(System.currentTimeMillis())
                .choices(List.of(choice))
                .usage(usage)
                .build();
    }

    /**
     * Gemini sometimes wraps JSON in ```json fences even in JSON mode.
     * Strip them so downstream Jackson parsing works.
     */
    private String stripJsonFence(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}