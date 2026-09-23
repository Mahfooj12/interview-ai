package com.interviewai.controller;

import com.interviewai.dto.report.GenerateReportResponse;
import com.interviewai.dto.report.ReportResponse;
import com.interviewai.dto.report.ReportSummaryResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Interview reports and learning roadmaps")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate/{interviewId}")
    @Operation(summary = "Generate or regenerate a report for an interview")
    public ResponseEntity<ApiResponse<GenerateReportResponse>> generate(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String interviewId,
            @RequestParam(name = "force", defaultValue = "false") boolean force) {
        GenerateReportResponse response = reportService.generate(user.getId(), interviewId, force);
        return ResponseEntity.ok(ApiResponse.ok(response, response.getMessage()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a report by id")
    public ResponseEntity<ApiResponse<ReportResponse>> get(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.get(user.getId(), id)));
    }

    @GetMapping("/by-interview/{interviewId}")
    @Operation(summary = "Get a report by interview id")
    public ResponseEntity<ApiResponse<ReportResponse>> getByInterview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String interviewId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getByInterview(user.getId(), interviewId)));
    }

    @GetMapping
    @Operation(summary = "List the current user's reports")
    public ResponseEntity<ApiResponse<List<ReportSummaryResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.list(user.getId())));
    }
}
