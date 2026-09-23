package com.interviewai.controller;

import com.interviewai.dto.resume.ResumeResponse;
import com.interviewai.dto.resume.ResumeSummaryResponse;
import com.interviewai.dto.resume.ResumeUploadResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resumes", description = "Resume upload, parsing and management")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a resume (PDF or DOCX)")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> upload(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("file") MultipartFile file) {
        ResumeUploadResponse response = resumeService.upload(user.getId(), file);
        return ResponseEntity.ok(ApiResponse.ok(response, "Resume uploaded"));
    }

    @GetMapping
    @Operation(summary = "List the current user's resumes")
    public ResponseEntity<ApiResponse<List<ResumeSummaryResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.list(user.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a resume by id")
    public ResponseEntity<ApiResponse<ResumeResponse>> get(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.get(user.getId(), id)));
    }

    @PostMapping("/{id}/primary")
    @Operation(summary = "Mark a resume as primary")
    public ResponseEntity<ApiResponse<ResumeResponse>> setPrimary(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.setPrimary(user.getId(), id), "Primary resume updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resume")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        resumeService.delete(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Resume deleted"));
    }
}
