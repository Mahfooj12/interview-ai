package com.interviewai.dto.interview;

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
public class InterviewSummaryResponse {
    private String id;
    private InterviewSession.Type type;
    private InterviewSession.Mode mode;
    private InterviewSession.Difficulty difficulty;
    private InterviewSession.Personality personality;
    private InterviewSession.Status status;
    private int totalQuestionsAsked;
    private double cumulativeScore;
    private Instant startedAt;
    private Instant completedAt;
    private long durationSec;
}