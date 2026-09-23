package com.interviewai.model;

import com.interviewai.model.embedded.AnswerMetrics;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.model.embedded.ReportSections;
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
import java.util.ArrayList;
import java.util.List;

@Document(collection = "reports")
@CompoundIndex(name = "user_generated_idx", def = "{'userId': 1, 'generatedAt': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    private String id;

    @Indexed(unique = true)
    private String interviewId;

    @Indexed
    private String userId;

    private ReportSections sections;

    @Builder.Default private List<String> strongAreas = new ArrayList<>();
    @Builder.Default private List<String> weakAreas = new ArrayList<>();
    @Builder.Default private List<QuestionFeedback> questionFeedback = new ArrayList<>();
    @Builder.Default private List<String> recommendedTopics = new ArrayList<>();
    @Builder.Default private List<LearningItem> learningRoadmap = new ArrayList<>();

    private AnswerMetrics communicationStats;

    private String summary;

    private Instant generatedAt;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class QuestionFeedback {
        private String questionId;
        private String question;
        private String answer;
        private double score;
        private String feedback;
        private String idealAnswer;
    }
}
