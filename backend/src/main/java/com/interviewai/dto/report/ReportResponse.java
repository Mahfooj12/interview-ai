package com.interviewai.dto.report;

import com.interviewai.model.embedded.AnswerMetrics;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.model.embedded.ReportSections;
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
public class ReportResponse {
    private String id;
    private String interviewId;
    private String userId;
    private ReportSections sections;
    private List<String> strongAreas;
    private List<String> weakAreas;
    private List<QuestionFeedbackResponse> questionFeedback;
    private List<String> recommendedTopics;
    private List<LearningItem> learningRoadmap;
    private AnswerMetrics communicationStats;
    private String summary;
    private Instant generatedAt;
}
