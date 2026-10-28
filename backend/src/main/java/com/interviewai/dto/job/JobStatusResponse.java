package com.interviewai.dto.job;

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
public class JobStatusResponse {
    private String jobId;
    private String status;         // CREATED | PROCESSING | COMPLETED | FAILED
    private String interviewId;
    private String questionId;
    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private String errorCode;
    private String errorMessage;
}
