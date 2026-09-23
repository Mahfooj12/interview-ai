package com.interviewai.dto.interview;

import com.interviewai.model.InterviewSession;
import com.interviewai.model.embedded.InterviewState;
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
public class InterviewResponse {
    private String id;
    private String userId;
    private String resumeId;
    private String jobDescriptionId;
    private InterviewSession.Type type;
    private InterviewSession.Mode mode;
    private InterviewSession.Difficulty difficulty;
    private InterviewSession.Personality personality;
    private InterviewSession.Status status;
    private InterviewState state;
    private List<String> questionIds;
    private String focusTopic;
    private Instant startedAt;
    private Instant completedAt;
    private long durationSec;
    private Instant createdAt;
    private Instant updatedAt;
}