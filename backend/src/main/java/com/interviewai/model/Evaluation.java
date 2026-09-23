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
import java.util.ArrayList;
import java.util.List;

@Document(collection = "evaluations")
@CompoundIndex(name = "interview_question_eval_idx", def = "{'interviewId': 1, 'questionId': 1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    private String id;

    @Indexed
    private String interviewId;

    @Indexed
    private String questionId;

    @Indexed
    private String answerId;

    private String userId;

    private double technicalAccuracy;
    private double relevance;
    private double depth;
    private double clarity;
    private double communication;
    private double confidence;
    private double overall;

    @Builder.Default private List<String> strengths = new ArrayList<>();
    @Builder.Default private List<String> weaknesses = new ArrayList<>();
    @Builder.Default private List<String> weakAreasDetected = new ArrayList<>();

    private String feedback;
    private String idealAnswer;
    private boolean followUpNeeded;

    private Instant createdAt;
}