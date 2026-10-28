package com.interviewai.repository;

import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.model.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewGenerationJobRepository
        extends MongoRepository<InterviewGenerationJob, String> {

    Optional<InterviewGenerationJob> findByIdAndUserId(String id, String userId);

    List<InterviewGenerationJob> findAllByStatusAndStartedAtBefore(JobStatus status, Instant cutoff);
}