package com.interviewai.dto.evaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationSummaryResponse {
    private double averageTechnical;
    private double averageCommunication;
    private double averageConfidence;
    private double averageDepth;
    private double averageRelevance;
    private double averageOverall;
    private int totalEvaluations;
}
