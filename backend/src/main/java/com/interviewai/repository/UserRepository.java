package com.interviewai.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.interviewai.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByStatus(String status);

    // findAll(Pageable) is inherited from MongoRepository — no method needed.
}