package com.interviewai.repository;

import com.interviewai.model.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends MongoRepository<Evaluation, String> {

    List<Evaluation> findAllByInterviewIdOrderByCreatedAtAsc(String interviewId);

    Page<Evaluation> findAllByInterviewId(String interviewId, Pageable pageable);

    Optional<Evaluation> findByAnswerId(String answerId);

    void deleteByInterviewId(String interviewId);
}