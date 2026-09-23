package com.interviewai.ai.client.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    private List<Candidate> candidates;

    @JsonProperty("usageMetadata")
    private UsageMetadata usageMetadata;

    @JsonProperty("modelVersion")
    private String modelVersion;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
        @JsonProperty("finishReason")
        private String finishReason;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private String role;
        private List<Part> parts;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageMetadata {
        @JsonProperty("promptTokenCount")
        private Integer promptTokenCount;
        @JsonProperty("candidatesTokenCount")
        private Integer candidatesTokenCount;
        @JsonProperty("totalTokenCount")
        private Integer totalTokenCount;
    }

    /**
     * Extract the first text part of the first candidate.
     */
    public String firstText() {
        if (candidates == null || candidates.isEmpty()) return null;
        Candidate c = candidates.get(0);
        if (c.getContent() == null || c.getContent().getParts() == null
                || c.getContent().getParts().isEmpty()) {
            return null;
        }
        return c.getContent().getParts().get(0).getText();
    }
}