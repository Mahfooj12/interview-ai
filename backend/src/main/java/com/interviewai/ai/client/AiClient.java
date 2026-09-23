package com.interviewai.ai.client;

import com.interviewai.ai.client.dto.ChatCompletionResponse;
import com.interviewai.ai.client.dto.ChatMessage;

import java.util.List;

/**
 * Provider-agnostic AI chat client.
 * Both {@link DeepSeekClient} and GeminiClient implement this so that
 * {@code AIServiceImpl} does not need to know which provider is active.
 */
public interface AiClient {

    /**
     * JSON-mode chat completion. The caller parses the returned content as JSON.
     */
    ChatCompletionResponse chatJson(List<ChatMessage> messages, double temperature, int maxTokens);

    /**
     * Plain-text chat completion.
     */
    ChatCompletionResponse chatText(List<ChatMessage> messages, double temperature, int maxTokens);

    /**
     * Provider identifier: "deepseek" or "gemini".
     */
    String provider();
}