package com.interviewai.model;

import com.interviewai.model.embedded.InterviewState;
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

@Document(collection = "interviewSessions")
@CompoundIndex(name = "user_status_started_idx", def = "{'userId': 1, 'status': 1, 'startedAt': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSession extends BaseAudit {

    public enum Type { TECHNICAL, HR, RESUME, PROJECT, SYSTEM_DESIGN, BEHAVIORAL }
    public enum Mode { TEXT, VOICE, VIDEO }
    public enum Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }
    public enum Personality { FRIENDLY, STRICT, PROFESSIONAL, TECHNICAL, HR_MANAGER }
    public enum Status { SCHEDULED, IN_PROGRESS, COMPLETED, ABANDONED }

    @Id
    private String id;

    @Indexed
    private String userId;

    private String resumeId;
    private String jobDescriptionId;

    private Type type;
    private Mode mode;
    private Difficulty difficulty;
    private Personality personality;

    private Status status;

    @Builder.Default private InterviewState state = new InterviewState();

    @Builder.Default private List<String> questionIds = new ArrayList<>();

    private String focusTopic;

    private Instant startedAt;
    private Instant completedAt;
    private long durationSec;
}