package com.interviewai.kafka;

import com.interviewai.dto.kafka.InterviewGenerationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewGenerationProducer {

    private static final String TOPIC = "interview-generation";

    private final KafkaTemplate<String, InterviewGenerationMessage> kafkaTemplate;

    public CompletableFuture<SendResult<String, InterviewGenerationMessage>> publish(
            InterviewGenerationMessage message) {

        var future = kafkaTemplate.send(
                TOPIC,
                message.getInterviewId(),   // partition key
                message
        );

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka publish FAILED jobId={} eventId={} err={}",
                        message.getJobId(), message.getEventId(), ex.getMessage(), ex);
            } else if (result != null) {
                var md = result.getRecordMetadata();
                log.info("Kafka publish OK jobId={} eventId={} topic={} partition={} offset={}",
                        message.getJobId(), message.getEventId(),
                        md.topic(), md.partition(), md.offset());
            }
        });

        return future;
    }
}
