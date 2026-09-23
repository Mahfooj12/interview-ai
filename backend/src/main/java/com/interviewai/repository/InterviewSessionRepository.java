package com.interviewai.repository;

import com.interviewai.model.InterviewSession;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends MongoRepository<InterviewSession, String> {

    @Query("{ 'userId': ?0 }")
    List<InterviewSession> findAllByUserId(String userId, Sort sort);

    default List<InterviewSession> findAllByUserIdOrderByStartedAtDesc(String userId) {
        return findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "startedAt"));
    }

    Optional<InterviewSession> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    long countByStatus(InterviewSession.Status status);
}