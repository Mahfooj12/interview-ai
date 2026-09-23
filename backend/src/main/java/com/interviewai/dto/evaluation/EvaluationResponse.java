package com.interviewai.dto.evaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private String id;
    private String interviewId;
    private String questionId;
    private String answerId;
    private String userId;
    private double technicalAccuracy;
    private double relevance;
    private double depth;
    private double clarity;
    private double communication;
    private double confidence;
    private double overall;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> weakAreasDetected;
    private String feedback;
    private String idealAnswer;
    private boolean followUpNeeded;
    private Instant createdAt;
}
