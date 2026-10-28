package com.interviewai.repository;

import com.interviewai.model.ProcessedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedEventRepository extends MongoRepository<ProcessedEvent, String> {

    boolean existsByEventId(String eventId);
}
