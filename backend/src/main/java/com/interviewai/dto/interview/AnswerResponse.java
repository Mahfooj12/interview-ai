package com.interviewai.dto.interview;

import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
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
public class AnswerResponse {
    private String interviewId;
    private String answerId;
    private String questionId;
    private Evaluation evaluation;
    private NextQuestionResponse nextQuestion;
    private InterviewSession.Status status;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private double cumulativeScore;
    private boolean completed;
}