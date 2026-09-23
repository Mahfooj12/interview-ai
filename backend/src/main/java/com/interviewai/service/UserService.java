package com.interviewai.service;

import com.interviewai.dto.auth.UserResponse;
import com.interviewai.model.User;

public interface UserService {

    User getById(String id);

    User getByEmail(String email);

    UserResponse getProfile(String userId);

    User save(User user);

    boolean existsByEmail(String email);
}