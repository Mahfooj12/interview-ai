package com.interviewai.worker;

import com.interviewai.dto.interview.NextQuestionResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.FatalJobException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.repository.InterviewGenerationJobRepository;
import com.interviewai.service.InterviewService;
import com.interviewai.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InterviewGenerationWorker {

    private static final Logger log = LoggerFactory.getLogger(InterviewGenerationWorker.class);

    /**
     * A stable identifier for this JVM instance — used to detect orphaned jobs
     * after a restart. Note: this is per-JVM, so it changes on every restart.
     */
    private final String instanceId = "instance-" + UUID.randomUUID();

    private final JobService jobService;
    private final InterviewService interviewService;
    private final InterviewGenerationJobRepository jobRepository;

    public InterviewGenerationWorker(JobService jobService,
                                     InterviewService interviewService,
                                     InterviewGenerationJobRepository jobRepository) {
        this.jobService = jobService;
        this.interviewService = interviewService;
        this.jobRepository = jobRepository;
    }

    /**
     * Purana Async method (REST API ke /start-async ke liye).
     * Ye khud hi job claim karta hai.
     */
    @Async("interviewGenerationExecutor")
    public void process(String jobId) {
        log.info("Worker [{}] starting async job {}", instanceId, jobId);

        // 1. Atomic claim — only one worker will succeed
        InterviewGenerationJob job = jobService.claimJob(jobId, instanceId);
        if (job == null) {
            log.info("Worker [{}] job {} already claimed — exiting", instanceId, jobId);
            return;
        }

        try {
            executeJobLogic(job);
        } catch (Exception e) {
            log.error("Worker [{}] job {} failed unexpectedly", instanceId, jobId, e);
            jobService.markFailed(jobId, "INTERNAL_ERROR", e.getMessage());
        }
    }

    /**
     * NAYA method: Kafka Consumer isko call karta hai.
     * Kafka pehle hi claim kar chuka hai, isliye yahan claim nahi karna hai.
     * Agar transient error aata hai, toh exception throw karna hai taaki consumer retry kar sake.
     */
    public void processJob(String jobId) {
        log.info("Worker [{}] processing Kafka job {}", instanceId, jobId);

        // Kafka ne claim kar diya hai, ab hum DB se job fetch karenge
        InterviewGenerationJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new FatalJobException("Job not found in DB: " + jobId));

        try {
            executeJobLogic(job);
        } catch (ResourceNotFoundException e) {
            jobService.markJobFailed(jobId, "INTERVIEW_NOT_FOUND", e.getMessage());
            throw new FatalJobException("Interview not found for job " + jobId, e); // Fatal -> DLQ
        } catch (BadRequestException e) {
            jobService.markJobFailed(jobId, "INVALID_STATE", e.getMessage());
            throw new FatalJobException("Invalid state for job " + jobId, e); // Fatal -> DLQ
        } catch (Exception e) {
            log.warn("Worker [{}] transient failure for job {}: {}", instanceId, jobId, e.getMessage());
            // Transient error hai, exception throw karo taaki Kafka consumer resetJobForRetry kare
            throw e; 
        }
    }

    /**
     * Common logic dono methods (Async aur Kafka) ke liye.
     */
    private void executeJobLogic(InterviewGenerationJob job) {
        String jobId = job.getId();
        
        NextQuestionResponse response = interviewService.start(
                job.getUserId(), job.getInterviewId());

        if (response == null || response.getQuestion() == null) {
            jobService.markJobFailed(jobId, "NO_QUESTION", "Interview start returned no question");
            return;
        }

        // 3. Mark COMPLETED
        jobService.markCompleted(jobId, response.getQuestion().getId());
        log.info("Worker [{}] job {} COMPLETED questionId={}", instanceId, jobId, response.getQuestion().getId());
    }

    public String getInstanceId() {
        return instanceId;
    }
}