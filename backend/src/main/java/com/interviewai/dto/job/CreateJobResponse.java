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
public class CreateJobResponse {
    private String jobId;
    private String status;         // "CREATED"
    private Instant createdAt;
    private String statusUrl;      // "/api/jobs/{jobId}"
}