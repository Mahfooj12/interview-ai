package com.interviewai.dto.progress;

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
public class ProgressPointResponse {
    private Instant date;
    private double overallScore;
    private double technicalScore;
    private double communicationScore;
    private double confidenceScore;
    private int interviewsCompleted;
}