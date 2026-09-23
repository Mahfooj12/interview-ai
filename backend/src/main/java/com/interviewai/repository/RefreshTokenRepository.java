package com.interviewai.repository;

import com.interviewai.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);

    void deleteByExpiresAtBefore(Instant cutoff);
}
