package com.interviewai.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewState {

    @Builder.Default private int currentQuestionIndex = 0;
    @Builder.Default private int totalQuestionsAsked = 0;
    private String currentTopic;
    @Builder.Default private List<String> weakAreas = new ArrayList<>();
    @Builder.Default private List<String> strongAreas = new ArrayList<>();
    @Builder.Default private double cumulativeScore = 0.0;
    private String rollingSummary;
    @Builder.Default private String lastQuestionId = null;
    @Builder.Default private boolean completed = false;
    private Instant lastUpdatedAt;
}