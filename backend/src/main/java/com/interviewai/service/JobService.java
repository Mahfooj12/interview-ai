package com.interviewai.service;

import com.interviewai.dto.job.JobStatusResponse;
import com.interviewai.model.InterviewGenerationJob;

public interface JobService {

    InterviewGenerationJob createJob(String userId, String interviewId);

    JobStatusResponse getStatus(String userId, String jobId);

    // Async ke liye purana method
    InterviewGenerationJob claimJob(String jobId, String instanceId);

    // Kafka ke liye naya method (boolean return karta hai)
    boolean claimJob(String jobId);

    void markCompleted(String jobId, String questionId);

    // Async ke liye purana method
    void markFailed(String jobId, String errorCode, String errorMessage);

    // Kafka aur Worker ke liye naya method
    void markJobFailed(String jobId, String errorCode, String errorMessage);

    // Kafka ke liye naya method
    void resetJobForRetry(String jobId);

    // Kafka ke liye naya methods (Idempotency)
    boolean isEventProcessed(String eventId);
    void markEventProcessed(String eventId, String jobId);
}