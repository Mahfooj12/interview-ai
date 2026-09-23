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
public class InterviewStateResponse {
    private String interviewId;
    private InterviewSession.Status status;
    private int currentQuestionIndex;
    private int totalQuestionsAsked;
    private String currentTopic;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private double cumulativeScore;
    private boolean completed;
}
