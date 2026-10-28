package com.interviewai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "interviewGenerationJobs")
@CompoundIndex(name = "user_status_created_idx", def = "{'userId': 1, 'status': 1, 'createdAt': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewGenerationJob {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String interviewId;

    private JobStatus status;

    private String questionId;    // populated when COMPLETED

    @CreatedDate
    private Instant createdAt;

    private Instant startedAt;
    private Instant completedAt;

    private String errorCode;
    private String errorMessage;

    /**
     * Identifies which backend instance claimed this job.
     * Used by the reaper to distinguish live vs orphaned PROCESSING jobs.
     */
    private String processingInstanceId;

    private int attemptCount;
}