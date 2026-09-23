package com.interviewai.dto.admin;

import com.interviewai.model.AiUsage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAiUsageResponse {
    private String id;
    private String userId;
    private AiUsage.Operation operation;
    private String model;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private long latencyMs;
    private boolean success;
    private String errorMessage;
    private Instant createdAt;
}