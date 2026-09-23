package com.interviewai.dto.interview;

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
public class NextQuestionResponse {
    private String interviewId;
    private InterviewSession.Status status;
    private QuestionResponse question;
    private int questionNumber;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private double cumulativeScore;
    private boolean completed;
    private String message;
}
