package com.interviewai.service.impl;

import com.interviewai.dto.job.JobStatusResponse;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.model.JobStatus;
import com.interviewai.model.ProcessedEvent;
import com.interviewai.repository.InterviewGenerationJobRepository;
import com.interviewai.repository.ProcessedEventRepository;
import com.interviewai.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final InterviewGenerationJobRepository jobRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final MongoTemplate mongoTemplate;

    public JobServiceImpl(InterviewGenerationJobRepository jobRepository,
                          ProcessedEventRepository processedEventRepository,
                          MongoTemplate mongoTemplate) {
        this.jobRepository = jobRepository;
        this.processedEventRepository = processedEventRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public InterviewGenerationJob createJob(String userId, String interviewId) {
        InterviewGenerationJob job = InterviewGenerationJob.builder()
                .userId(userId)
                .interviewId(interviewId)
                .status(JobStatus.CREATED)
                .createdAt(Instant.now())
                .attemptCount(0)
                .build();
        job = jobRepository.save(job);
        log.info("Job created id={} userId={} interviewId={}", job.getId(), userId, interviewId);
        return job;
    }

    @Override
    public JobStatusResponse getStatus(String userId, String jobId) {
        InterviewGenerationJob job = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return toResponse(job);
    }

    @Override
    public InterviewGenerationJob claimJob(String jobId, String instanceId) {
        Query query = Query.query(
                Criteria.where("_id").is(jobId)
                        .and("status").is(JobStatus.CREATED));

        Update update = new Update()
                .set("status", JobStatus.PROCESSING)
                .set("startedAt", Instant.now())
                .set("processingInstanceId", instanceId)
                .inc("attemptCount", 1);

        InterviewGenerationJob claimed = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), InterviewGenerationJob.class);

        if (claimed == null) {
            log.info("Job {} could not be claimed (already PROCESSING/COMPLETED/FAILED)", jobId);
        }
        return claimed;
    }

    // NAYA: Kafka ke liye boolean return karne wala claim method
    @Override
    public boolean claimJob(String jobId) {
        Query query = Query.query(
                Criteria.where("_id").is(jobId)
                        .and("status").is(JobStatus.CREATED));

        Update update = new Update()
                .set("status", JobStatus.PROCESSING)
                .set("startedAt", Instant.now())
                .inc("attemptCount", 1);

        InterviewGenerationJob claimed = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), InterviewGenerationJob.class);
        
        return claimed != null;
    }

    @Override
    public void markCompleted(String jobId, String questionId) {
        Query query = Query.query(Criteria.where("_id").is(jobId));
        Update update = new Update()
                .set("status", JobStatus.COMPLETED)
                .set("completedAt", Instant.now())
                .set("questionId", questionId)
                .unset("processingInstanceId");

        mongoTemplate.updateFirst(query, update, InterviewGenerationJob.class);
        log.info("Job {} marked COMPLETED questionId={}", jobId, questionId);
    }

    @Override
    public void markFailed(String jobId, String errorCode, String errorMessage) {
        markJobFailed(jobId, errorCode, errorMessage);
    }

    // NAYA: Kafka ke liye markJobFailed
    @Override
    public void markJobFailed(String jobId, String errorCode, String errorMessage) {
        Query query = Query.query(Criteria.where("_id").is(jobId));
        Update update = new Update()
                .set("status", JobStatus.FAILED)
                .set("completedAt", Instant.now())
                .set("errorCode", errorCode)
                .set("errorMessage", truncate(errorMessage, 1000))
                .unset("processingInstanceId");

        mongoTemplate.updateFirst(query, update, InterviewGenerationJob.class);
        log.warn("Job {} marked FAILED code={} msg={}", jobId, errorCode, truncate(errorMessage, 200));
    }

    // NAYA: Transient error aane par job ko retry ke liye wapas CREATED karne ke liye
    @Override
    public void resetJobForRetry(String jobId) {
        Query query = Query.query(Criteria.where("_id").is(jobId).and("status").is(JobStatus.PROCESSING));
        Update update = new Update()
                .set("status", JobStatus.CREATED)
                .unset("startedAt")
                .unset("processingInstanceId");
        mongoTemplate.updateFirst(query, update, InterviewGenerationJob.class);
        log.info("Job {} reset for retry (back to CREATED)", jobId);
    }

    // NAYA: Idempotency check
    @Override
    public boolean isEventProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    // NAYA: Mark event as processed
    @Override
    public void markEventProcessed(String eventId, String jobId) {
        ProcessedEvent event = new ProcessedEvent(eventId, jobId, Instant.now());
        try {
            processedEventRepository.save(event);
        } catch (Exception e) {
            log.debug("Event {} already marked processed (concurrent save)", eventId);
        }
    }

    private JobStatusResponse toResponse(InterviewGenerationJob job) {
        return JobStatusResponse.builder()
                .jobId(job.getId())
                .status(job.getStatus().name())
                .interviewId(job.getInterviewId())
                .questionId(job.getQuestionId())
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .errorCode(job.getErrorCode())
                .errorMessage(job.getErrorMessage())
                .build();
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}