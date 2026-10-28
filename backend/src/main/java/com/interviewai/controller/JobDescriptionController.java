package com.interviewai.controller;

import com.interviewai.dto.job.JobDescriptionRequest;
import com.interviewai.dto.job.JobDescriptionResponse;
import com.interviewai.dto.job.JobDescriptionSummaryResponse;
import com.interviewai.dto.job.JobMatchResponse;
import com.interviewai.dto.common.PageResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.JobDescriptionService;
import com.interviewai.util.PageRequestBuilder;
import com.interviewai.dto.common.PageRequestParams;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/jobs")
@Validated
@Tag(name = "Job Descriptions", description = "Job description ingestion, parsing and resume matching")
public class JobDescriptionController {

    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "title", "company");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final JobDescriptionService jobService;

    public JobDescriptionController(JobDescriptionService jobService) {
        this.jobService = jobService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a job description from pasted text")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> createFromText(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody JobDescriptionRequest request) {
        JobDescriptionResponse response = jobService.createFromText(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Job description created"));
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a job description from an uploaded PDF/DOCX file")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> createFromFile(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "resumeId", required = false) String resumeId) {
        JobDescriptionResponse response = jobService.createFromFile(user.getId(), file, resumeId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Job description created from file"));
    }

    @GetMapping
    @Operation(summary = "List the current user's job descriptions (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<JobDescriptionSummaryResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute PageRequestParams params) {

        Pageable pageable = PageRequestBuilder.build(
                params, ALLOWED_SORT, DEFAULT_SORT, "_id");

        return ResponseEntity.ok(ApiResponse.ok(
                jobService.listPaged(user.getId(), pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job description by id")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> get(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.get(user.getId(), id)));
    }

    @PostMapping("/{id}/match")
    @Operation(summary = "Match a job description with a resume using AI")
    public ResponseEntity<ApiResponse<JobMatchResponse>> match(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id,
            @RequestParam("resumeId") String resumeId) {
        JobMatchResponse response = jobService.match(user.getId(), id, resumeId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Match computed"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job description")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        jobService.delete(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Job description deleted"));
    }
}