package com.interviewai.dto.evaluation;

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
public class InterviewEvaluationsResponse {
    private String interviewId;
    private List<EvaluationResponse> evaluations;
    private EvaluationSummaryResponse summary;
}