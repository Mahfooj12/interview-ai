package com.interviewai.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class ChatCompletionResponse {

    private String id;
    private String model;
    private Long created;
    private List<ChatChoice> choices;
    private ChatUsage usage;

    public String firstContent() {
        if (choices == null || choices.isEmpty()) return null;
        ChatChoice choice = choices.get(0);
        return choice.getMessage() != null ? choice.getMessage().getContent() : null;
    }
}
