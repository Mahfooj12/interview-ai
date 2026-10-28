package com.interviewai.config;

import com.interviewai.dto.kafka.InterviewGenerationMessage;
import com.interviewai.exception.FatalJobException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Profile("!test")
public class KafkaConfig {

    @Value("${app.kafka.topic.interview-generation:interview-generation}")
    private String topicName;

    @Value("${app.kafka.retry.max-attempts:3}")
    private long maxRetries;

    @Value("${app.kafka.retry.backoff-ms:2000}")
    private long backoffMs;

    /**
     * Automatically creates the topic when the app starts.
     */
    @Bean
    public NewTopic interviewGenerationTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Error handler: Fatal errors -> DLQ. Transient errors -> Retry then DLQ.
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, InterviewGenerationMessage> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + "-dlq", record.partition())
        );

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(backoffMs, maxRetries));
        handler.addNotRetryableExceptions(FatalJobException.class);

        return handler;
    }

    /**
     * Consumer factory with manual acknowledgment.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InterviewGenerationMessage> interviewGenerationKafkaListenerContainerFactory(
            ConsumerFactory<String, InterviewGenerationMessage> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, InterviewGenerationMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3); // 3 partitions ke liye 3 threads
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler);
        
        return factory;
    }
}