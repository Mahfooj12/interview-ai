package com.interviewai.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlqMessage {

    private String originalTopic;
    private int originalPartition;
    private long originalOffset;
    private String originalPayload;
    private String exceptionClass;
    private String exceptionMessage;
    private String stackTrace;
    private Instant failedAt;
    private int attemptCount;
}
