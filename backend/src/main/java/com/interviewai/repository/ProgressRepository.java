package com.interviewai.repository;

import com.interviewai.model.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends MongoRepository<Progress, String> {

    List<Progress> findAllByUserIdOrderByDateAsc(String userId);

    List<Progress> findAllByUserIdAndDateAfterOrderByDateAsc(String userId, Instant after);

    Optional<Progress> findByUserIdAndDate(String userId, Instant date);

    void deleteByUserId(String userId);
}