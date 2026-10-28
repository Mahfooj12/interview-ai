package com.interviewai.controller;

import com.interviewai.dto.job.JobStatusResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview-jobs")
@Tag(name = "Jobs", description = "Background job status")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get the status of a background job")
    public ResponseEntity<ApiResponse<JobStatusResponse>> getStatus(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String jobId) {
        JobStatusResponse response = jobService.getStatus(user.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
