package com.interviewai.controller;

import org.springframework.data.domain.Sort;
import com.interviewai.dto.admin.AdminAiUsageResponse;
import com.interviewai.dto.admin.AdminInterviewResponse;
import com.interviewai.dto.admin.AdminStatisticsResponse;
import com.interviewai.dto.admin.AdminUserResponse;
import com.interviewai.dto.admin.AdminUserUpdateRequest;
import com.interviewai.dto.common.PageRequestParams;
import com.interviewai.dto.common.PageResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.service.AdminService;
import com.interviewai.util.PageRequestBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Validated
@Tag(name = "Admin", description = "Platform administration endpoints")
public class AdminController {

    private final AdminService adminService;

    private static final Set<String> USER_SORT = Set.of("createdAt", "email", "lastLoginAt");
    private static final Sort USER_DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private static final Set<String> INTERVIEW_SORT = Set.of("startedAt", "completedAt", "status");
    private static final Sort INTERVIEW_DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "startedAt");

    private static final Set<String> AI_USAGE_SORT = Set.of("createdAt", "totalTokens", "latencyMs");
    private static final Sort AI_USAGE_DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/statistics")
    @Operation(summary = "Aggregated platform statistics")
    public ResponseEntity<ApiResponse<AdminStatisticsResponse>> statistics() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.statistics()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> users(
            @Valid @ModelAttribute PageRequestParams params) {
        Pageable pageable = PageRequestBuilder.build(params, USER_SORT, USER_DEFAULT_SORT, "_id");
        return ResponseEntity.ok(ApiResponse.ok(adminService.listUsers(pageable)));
    }


    @PutMapping("/users/{id}")
    @Operation(summary = "Update a user's status, plan, or roles")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.updateUser(id, request), "User updated"));
    }

    @GetMapping("/interviews")
    public ResponseEntity<ApiResponse<PageResponse<AdminInterviewResponse>>> interviews(
            @Valid @ModelAttribute PageRequestParams params) {
        Pageable pageable = PageRequestBuilder.build(
                params, INTERVIEW_SORT, INTERVIEW_DEFAULT_SORT, "_id");
        return ResponseEntity.ok(ApiResponse.ok(adminService.listInterviews(pageable)));
    }

    @GetMapping("/ai-usage")
    public ResponseEntity<ApiResponse<PageResponse<AdminAiUsageResponse>>> aiUsage(
            @Valid @ModelAttribute PageRequestParams params) {
        Pageable pageable = PageRequestBuilder.build(
                params, AI_USAGE_SORT, AI_USAGE_DEFAULT_SORT, "_id");
        return ResponseEntity.ok(ApiResponse.ok(adminService.listAiUsage(pageable)));
    }
}