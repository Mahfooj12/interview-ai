package com.interviewai.dto.interview;

import com.interviewai.model.Question;
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
public class QuestionResponse {
    private String id;
    private String interviewId;
    private int order;
    private Question.Type type;
    private String text;
    private List<String> expectedTopics;
    private String difficulty;
    private Question.GeneratedFrom generatedFrom;
    private String parentQuestionId;
    private Instant createdAt;
}
