package com.interviewai.dto.report;

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
public class ReportSummaryResponse {
    private String id;
    private String interviewId;
    private double overallScore;
    private double technical;
    private double communication;
    private double confidence;
    private double problemSolving;
    private Instant generatedAt;
}