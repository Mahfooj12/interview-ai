package com.interviewai.dto.admin;

import com.interviewai.model.InterviewSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInterviewResponse {
    private String id;
    private String userId;
    private String userEmail;
    private InterviewSession.Type type;
    private InterviewSession.Mode mode;
    private InterviewSession.Difficulty difficulty;
    private InterviewSession.Status status;
    private int totalQuestionsAsked;
    private double cumulativeScore;
    private Instant startedAt;
    private Instant completedAt;
    private long durationSec;
}
