package com.interviewai.repository;

import com.interviewai.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {

    List<Answer> findAllByInterviewIdOrderByAnsweredAtAsc(String interviewId);

    long countByInterviewId(String interviewId);

    void deleteByInterviewId(String interviewId);
}
