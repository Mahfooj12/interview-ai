package com.interviewai.repository;

import com.interviewai.model.Evaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends MongoRepository<Evaluation, String> {

    List<Evaluation> findAllByInterviewIdOrderByCreatedAtAsc(String interviewId);

    void deleteByInterviewId(String interviewId);
}
