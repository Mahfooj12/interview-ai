package com.interviewai.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest {

    private String model;

    private List<ChatMessage> messages;

    private Double temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @JsonProperty("stream")
    private Boolean stream;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseFormat {
        private String type; // "json_object" for JSON mode
    }
}