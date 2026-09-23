package com.interviewai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "aiUsage")
@CompoundIndex(name = "user_created_ai_idx", def = "{'userId': 1, 'createdAt': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUsage {

    public enum Operation {
        ANALYZE_RESUME,
        ANALYZE_JOB_DESCRIPTION,
        MATCH_RESUME_JD,
        GENERATE_QUESTION,
        GENERATE_FOLLOW_UP,
        EVALUATE_ANSWER,
        GENERATE_REPORT,
        GENERATE_LEARNING_PLAN
    }

    @Id
    private String id;

    @Indexed
    private String userId;

    private Operation operation;

    private String model;

    private int promptTokens;
    private int completionTokens;
    private int totalTokens;

    private long latencyMs;

    private boolean success;

    private String errorMessage;

    private Instant createdAt;
}
