package com.interviewai.repository;

import com.interviewai.model.Report;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    @Query("{ 'userId': ?0 }")
    List<Report> findAllByUserId(String userId, Sort sort);

    default List<Report> findAllByUserIdOrderByGeneratedAtDesc(String userId) {
        return findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "generatedAt"));
    }

    Optional<Report> findByInterviewId(String interviewId);

    Optional<Report> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);
}
