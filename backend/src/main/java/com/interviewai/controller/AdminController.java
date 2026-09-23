package com.interviewai.controller;

import com.interviewai.dto.admin.AdminAiUsageResponse;
import com.interviewai.dto.admin.AdminInterviewResponse;
import com.interviewai.dto.admin.AdminStatisticsResponse;
import com.interviewai.dto.admin.AdminUserResponse;
import com.interviewai.dto.admin.AdminUserUpdateRequest;
import com.interviewai.exception.ApiResponse;
import com.interviewai.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Platform administration endpoints")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/statistics")
    @Operation(summary = "Aggregated platform statistics")
    public ResponseEntity<ApiResponse<AdminStatisticsResponse>> statistics() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.statistics()));
    }

    @GetMapping("/users")
    @Operation(summary = "List all users with summary counts")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> users() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.listUsers()));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update a user's status, plan, or roles")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.updateUser(id, request), "User updated"));
    }

    @GetMapping("/interviews")
    @Operation(summary = "List recent interviews across all users")
    public ResponseEntity<ApiResponse<List<AdminInterviewResponse>>> interviews(
            @RequestParam(name = "limit", defaultValue = "50") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.listInterviews(limit)));
    }

    @GetMapping("/ai-usage")
    @Operation(summary = "List recent AI usage records")
    public ResponseEntity<ApiResponse<List<AdminAiUsageResponse>>> aiUsage(
            @RequestParam(name = "limit", defaultValue = "100") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.listAiUsage(limit)));
    }
}
