package com.interviewai.service;

import com.interviewai.dto.admin.AdminAiUsageResponse;
import com.interviewai.dto.admin.AdminInterviewResponse;
import com.interviewai.dto.admin.AdminStatisticsResponse;
import com.interviewai.dto.admin.AdminUserResponse;
import com.interviewai.dto.admin.AdminUserUpdateRequest;
import com.interviewai.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    AdminStatisticsResponse statistics();

    // NEW (replaces listUsers)
    PageResponse<AdminUserResponse> listUsers(Pageable pageable);

    AdminUserResponse updateUser(String userId, AdminUserUpdateRequest request);

    // NEW (replaces listInterviews(int))
    PageResponse<AdminInterviewResponse> listInterviews(Pageable pageable);

    // NEW (replaces listAiUsage(int))
    PageResponse<AdminAiUsageResponse> listAiUsage(Pageable pageable);
}