package com.interviewai.ai.client;

import com.interviewai.ai.client.dto.ChatCompletionRequest;
import com.interviewai.ai.client.dto.ChatCompletionResponse;
import com.interviewai.ai.client.dto.ChatMessage;
import com.interviewai.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DeepSeekClient implements AiClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    private final RestClient client;
    private final String model;

    public DeepSeekClient(RestClient deepSeekRestClient,
                          @Value("${app.ai.deepseek.model}") String model) {
        this.client = deepSeekRestClient;
        this.model = model;
    }

    @Override
    public ChatCompletionResponse chatJson(
            List<ChatMessage> messages,
            double temperature,
            int maxTokens) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .topP(1.0)
                .stream(false)
                .responseFormat(ChatCompletionRequest.ResponseFormat.builder()
                        .type("json_object")
                        .build())
                .build();

        return execute(request);
    }

    @Override
    public ChatCompletionResponse chatText(
            List<ChatMessage> messages,
            double temperature,
            int maxTokens) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .stream(false)
                .build();

        return execute(request);
    }

    @Override
    public String provider() {
        return "deepseek";
    }

    private ChatCompletionResponse execute(ChatCompletionRequest request) {
        long start = System.currentTimeMillis();
        try {
            ChatCompletionResponse response = client.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String body = new String(res.getBody().readAllBytes());
                        log.error("DeepSeek error status={} body={}", res.getStatusCode(), body);
                        throw new BadRequestException("AI provider error: " + res.getStatusCode());
                    })
                    .body(ChatCompletionResponse.class);

            if (response == null || response.firstContent() == null) {
                throw new BadRequestException("AI provider returned an empty response");
            }
            log.debug("DeepSeek call ok in {} ms", System.currentTimeMillis() - start);
            return response;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek call failed: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to call AI provider: " + e.getMessage());
        }
    }
}
