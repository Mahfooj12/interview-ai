package com.interviewai.dto.interview;

import com.interviewai.model.InterviewSession;
import jakarta.validation.constraints.NotNull;
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
public class CreateInterviewRequest {

    private String resumeId;           // required — falls back to primary resume

    private String jobDescriptionId;   // optional

    @NotNull(message = "Interview type is required")
    private InterviewSession.Type type;

    @NotNull(message = "Interview mode is required")
    private InterviewSession.Mode mode;

    @NotNull(message = "Difficulty is required")
    private InterviewSession.Difficulty difficulty;

    @NotNull(message = "Interviewer personality is required")
    private InterviewSession.Personality personality;

    /**
     * Optional focus topic — used by "Practice Weak Areas" flows.
     */
    private String focusTopic;
}