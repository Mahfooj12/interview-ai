package com.interviewai.mapper;

import com.interviewai.dto.auth.UserResponse;
import com.interviewai.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .plan(user.getPlan())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}