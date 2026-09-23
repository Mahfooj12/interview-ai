package com.interviewai.controller;

import com.interviewai.dto.progress.ProgressSkillResponse;
import com.interviewai.dto.progress.ProgressSummaryResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@Tag(name = "Progress", description = "Aggregated user progress and skill analytics")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    @Operation(summary = "Get aggregated progress summary for the current user")
    public ResponseEntity<ApiResponse<ProgressSummaryResponse>> summary(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(progressService.summary(user.getId())));
    }

    @GetMapping("/skills")
    @Operation(summary = "Get aggregated skill statistics for the current user")
    public ResponseEntity<ApiResponse<List<ProgressSkillResponse>>> skills(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(progressService.skills(user.getId())));
    }
}