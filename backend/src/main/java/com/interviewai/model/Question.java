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

@Document(collection = "questions")
@CompoundIndex(name = "interview_order_idx", def = "{'interviewId': 1, 'order': 1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    public enum Type { BASIC, SCENARIO, PROBLEM_SOLVING, FOLLOW_UP, TRUTH_TEST }
    public enum GeneratedFrom { RESUME, JD, PREVIOUS_ANSWER, CLAIM, FOCUS_TOPIC }

    @Id
    private String id;

    @Indexed
    private String interviewId;

    private int order;

    private Type type;
    private String text;

    @Builder.Default private List<String> expectedTopics = new ArrayList<>();

    private String difficulty;
    private GeneratedFrom generatedFrom;
    private String parentQuestionId;

    private Instant createdAt;
}