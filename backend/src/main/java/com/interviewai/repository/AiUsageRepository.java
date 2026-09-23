package com.interviewai.repository;

import com.interviewai.model.AiUsage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AiUsageRepository extends MongoRepository<AiUsage, String> {

    @Query("{ 'userId': ?0 }")
    List<AiUsage> findAllByUserId(String userId, Sort sort);

    default List<AiUsage> findAllByUserIdOrderByCreatedAtDesc(String userId) {
        return findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    List<AiUsage> findAllByCreatedAtAfter(Instant after);

    long countByCreatedAtAfter(Instant after);
}