package com.interviewai.service.impl;

import com.interviewai.dto.auth.UserResponse;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.UserMapper;
import com.interviewai.model.User;
import com.interviewai.repository.UserRepository;
import com.interviewai.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public UserResponse getProfile(String userId) {
        return userMapper.toResponse(getById(userId));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
