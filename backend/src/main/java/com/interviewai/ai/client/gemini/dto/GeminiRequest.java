package com.interviewai.ai.client.gemini.dto;

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
public class GeminiRequest {

    private List<Content> contents;

    @JsonProperty("systemInstruction")
    private Content systemInstruction;

    @JsonProperty("generationConfig")
    private GenerationConfig generationConfig;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        private String role;                 // "user" | "model" (system uses systemInstruction)
        private List<Part> parts;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Part {
        private String text;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GenerationConfig {
        private Double temperature;
        @JsonProperty("maxOutputTokens")
        private Integer maxOutputTokens;
        @JsonProperty("responseMimeType")
        private String responseMimeType;    // "application/json" for JSON mode
        @JsonProperty("topP")
        private Double topP;
    }
}