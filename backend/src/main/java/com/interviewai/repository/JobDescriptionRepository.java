package com.interviewai.repository;

import com.interviewai.model.JobDescription;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobDescriptionRepository extends MongoRepository<JobDescription, String> {

    @Query("{ 'userId': ?0 }")
    List<JobDescription> findAllByUserId(String userId, Sort sort);

    default List<JobDescription> findAllByUserIdOrderByCreatedAtDesc(String userId) {
        return findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    Optional<JobDescription> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);
}
