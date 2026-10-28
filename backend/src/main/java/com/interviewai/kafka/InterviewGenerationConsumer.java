package com.interviewai.kafka;

import com.interviewai.dto.kafka.InterviewGenerationMessage;
import com.interviewai.exception.FatalJobException;
import com.interviewai.service.JobService;
import com.interviewai.worker.InterviewGenerationWorker;
import org.springframework.context.annotation.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class InterviewGenerationConsumer {

    private final JobService jobService;
    private final InterviewGenerationWorker worker;

    @KafkaListener(
            topics = "interview-generation",
            groupId = "interview-generation-consumers",
            containerFactory = "interviewGenerationKafkaListenerContainerFactory"
    )
    public void consume(InterviewGenerationMessage message, Acknowledgment ack) {

        log.info("Kafka consume START jobId={} eventId={} interviewId={} attempt={}",
                message.getJobId(), message.getEventId(),
                message.getInterviewId(), message.getAttemptCount());

        // ---- LAYER 1: event idempotency ----
        if (jobService.isEventProcessed(message.getEventId())) {
            log.info("Event {} already processed, skipping", message.getEventId());
            ack.acknowledge();
            return;
        }

        // ---- LAYER 2: atomic job claim ----
        boolean claimed;
        try {
            claimed = jobService.claimJob(message.getJobId());
        } catch (Exception e) {
            log.error("Fatal claim error jobId={}: {}", message.getJobId(), e.getMessage());
            throw new FatalJobException("Cannot claim job " + message.getJobId(), e);
        }

        if (!claimed) {
            log.info("Job {} not claimable (already PROCESSING/COMPLETED). Marking event done.",
                    message.getJobId());
            jobService.markEventProcessed(message.getEventId(), message.getJobId());
            ack.acknowledge();
            return;
        }

        // ---- Process ----
        try {
            worker.processJob(message.getJobId());
        } catch (FatalJobException e) {
            log.error("Fatal processing error jobId={}: {}", message.getJobId(), e.getMessage());
            jobService.markJobFailed(message.getJobId(), "FATAL", e.getMessage());
            // Do NOT mark event processed — DLQ will hold it for investigation
            throw e;
        } catch (Exception e) {
            log.warn("Transient processing error jobId={} attempt={}: {}",
                    message.getJobId(), message.getAttemptCount(), e.getMessage());
            jobService.resetJobForRetry(message.getJobId());
            throw e;
        }

        // ---- Success ----
        jobService.markEventProcessed(message.getEventId(), message.getJobId());
        ack.acknowledge();

        log.info("Kafka consume DONE jobId={} eventId={}",
                message.getJobId(), message.getEventId());
    }
}