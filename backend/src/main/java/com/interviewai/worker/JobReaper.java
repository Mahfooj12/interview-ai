package com.interviewai.worker;

import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.model.JobStatus;
import com.interviewai.repository.InterviewGenerationJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class JobReaper {

    private static final Logger log = LoggerFactory.getLogger(JobReaper.class);

    private final InterviewGenerationJobRepository jobRepository;
    private final InterviewGenerationWorker worker;

    @Value("${app.async.stuck-job-threshold-seconds:300}")
    private long stuckThresholdSeconds;

    public JobReaper(InterviewGenerationJobRepository jobRepository,
                     InterviewGenerationWorker worker) {
        this.jobRepository = jobRepository;
        this.worker = worker;
    }

    /**
     * Every 60s, find jobs stuck in PROCESSING for longer than the threshold.
     * If they belong to this instance, they're probably orphaned — mark FAILED
     * and optionally re-dispatch. If they belong to another instance, log it
     * (this instance can't know if the other instance is alive).
     *
     * Note: In a multi-instance deployment, a better solution is a
     * distributed lock or a "heartbeat" field. For now, we simply fail
     * jobs older than the threshold and let the client retry.
     */
    @Scheduled(fixedDelayString = "${app.async.reaper-interval-ms:60000}")
    public void reapStuckJobs() {
        Instant cutoff = Instant.now().minusSeconds(stuckThresholdSeconds);
        List<InterviewGenerationJob> stuck =
                jobRepository.findAllByStatusAndStartedAtBefore(JobStatus.PROCESSING, cutoff);

        if (stuck.isEmpty()) return;

        for (InterviewGenerationJob job : stuck) {
            // Only auto-fail our own orphans. Other-instance jobs are logged.
            if (worker.getInstanceId().equals(job.getProcessingInstanceId())) {
                log.warn("Reaper marking orphaned job {} as FAILED", job.getId());
                job.setStatus(JobStatus.FAILED);
                job.setCompletedAt(Instant.now());
                job.setErrorCode("STUCK");
                job.setErrorMessage("Job timed out — worker likely crashed");
                jobRepository.save(job);
            } else {
                log.warn("Reaper sees stuck job {} owned by another instance ({})",
                        job.getId(), job.getProcessingInstanceId());
            }
        }
    }
}
