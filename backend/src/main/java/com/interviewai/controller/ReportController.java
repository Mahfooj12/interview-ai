package com.interviewai.controller;
import jakarta.validation.Valid;
import com.interviewai.dto.common.PageRequestParams;
import com.interviewai.dto.common.PageResponse;
import com.interviewai.dto.report.GenerateReportResponse;
import com.interviewai.dto.report.ReportResponse;
import com.interviewai.dto.report.ReportSummaryResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.ReportService;
import com.interviewai.util.PageRequestBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@RestController
@RequestMapping("/api/reports")
@Validated
@Tag(name = "Reports", description = "Interview reports and learning roadmaps")
public class ReportController {

    private static final Set<String> ALLOWED_SORT = Set.of("generatedAt", "overallScore", "createdAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "generatedAt");

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate/{interviewId}")
    public ResponseEntity<ApiResponse<GenerateReportResponse>> generate(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String interviewId,
            @RequestParam(name = "force", defaultValue = "false") boolean force) {
        GenerateReportResponse response = reportService.generate(user.getId(), interviewId, force);
        return ResponseEntity.ok(ApiResponse.ok(response, response.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> get(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.get(user.getId(), id)));
    }

    @GetMapping("/by-interview/{interviewId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getByInterview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String interviewId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getByInterview(user.getId(), interviewId)));
    }

    @GetMapping
    @Operation(summary = "List the current user's reports (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<ReportSummaryResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute PageRequestParams params) {

        Pageable pageable = PageRequestBuilder.build(
                params, ALLOWED_SORT, DEFAULT_SORT, "_id");

        return ResponseEntity.ok(ApiResponse.ok(
                reportService.listPaged(user.getId(), pageable)));
    }
}