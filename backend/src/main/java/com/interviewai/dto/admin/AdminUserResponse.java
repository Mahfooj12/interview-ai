package com.interviewai.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private String id;
    private String name;
    private String email;
    private Set<String> roles;
    private String plan;
    private String status;
    private Instant lastLoginAt;
    private Instant createdAt;
    private long interviewCount;
    private long reportCount;
}
