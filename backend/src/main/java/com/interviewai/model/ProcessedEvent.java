package com.interviewai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "processed_events")
public class ProcessedEvent {

    /** eventId from the Kafka message — also the idempotency key. */
    @Id
    private String eventId;

    @Indexed
    private String jobId;

    private Instant processedAt;
}
