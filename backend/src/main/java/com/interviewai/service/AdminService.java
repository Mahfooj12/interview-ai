package com.interviewai.service;

import com.interviewai.dto.admin.AdminAiUsageResponse;
import com.interviewai.dto.admin.AdminInterviewResponse;
import com.interviewai.dto.admin.AdminStatisticsResponse;
import com.interviewai.dto.admin.AdminUserResponse;
import com.interviewai.dto.admin.AdminUserUpdateRequest;

import java.util.List;

public interface AdminService {

    AdminStatisticsResponse statistics();

    List<AdminUserResponse> listUsers();

    AdminUserResponse updateUser(String userId, AdminUserUpdateRequest request);

    List<AdminInterviewResponse> listInterviews(int limit);

    List<AdminAiUsageResponse> listAiUsage(int limit);
}