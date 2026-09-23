package com.interviewai.repository;

import com.interviewai.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    List<Question> findAllByInterviewIdOrderByOrderAsc(String interviewId);

    long countByInterviewId(String interviewId);

    void deleteByInterviewId(String interviewId);
}
