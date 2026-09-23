package com.interviewai.controller;

import com.interviewai.dto.resume.ResumeAnalysisResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume Analysis", description = "AI-driven resume analysis")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @PostMapping("/{id}/analyze")
    @Operation(summary = "Analyze a resume with AI and store the parsed JSON")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> analyze(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id,
            @RequestParam(name = "force", defaultValue = "false") boolean force) {
        ResumeAnalysisResponse response = resumeAnalysisService.analyze(user.getId(), id, force);
        return ResponseEntity.ok(ApiResponse.ok(response, response.getMessage()));
    }
}