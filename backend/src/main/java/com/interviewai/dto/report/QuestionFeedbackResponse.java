package com.interviewai.dto.report;

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
public class QuestionFeedbackResponse {
    private String questionId;
    private String question;
    private String answer;
    private double score;
    private String feedback;
    private String idealAnswer;
}