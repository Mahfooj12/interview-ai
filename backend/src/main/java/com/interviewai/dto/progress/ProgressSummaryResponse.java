package com.interviewai.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressSummaryResponse {

    private long totalInterviews;
    private long completedInterviews;
    private double averageScore;
    private double bestScore;
    private double averageTechnical;
    private double averageCommunication;
    private double averageConfidence;
    private double averageProblemSolving;

    @Builder.Default private List<String> topStrongAreas = new ArrayList<>();
    @Builder.Default private List<String> topWeakAreas = new ArrayList<>();
    @Builder.Default private List<ProgressPointResponse> timeline = new ArrayList<>();
}