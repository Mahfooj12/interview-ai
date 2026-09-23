package com.interviewai.repository;

import com.interviewai.model.Resume;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {

    @Query("{ 'userId': ?0 }")
    List<Resume> findAllByUserId(String userId, Sort sort);

    default List<Resume> findAllByUserIdOrderByCreatedAtDesc(String userId) {
        return findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    Optional<Resume> findByIdAndUserId(String id, String userId);

    Optional<Resume> findFirstByUserIdAndPrimaryResumeTrue(String userId);

    long countByUserId(String userId);
}