package com.interviewai.controller;

import com.interviewai.dto.common.PageRequestParams;
import com.interviewai.dto.common.PageResponse;
import com.interviewai.dto.evaluation.EvaluationResponse;
import com.interviewai.exception.ApiResponse;

import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.EvaluationService;
import com.interviewai.util.PageRequestBuilder;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/interviews")
@Validated
@Tag(name = "Evaluations", description = "Per-answer AI evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "overall");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "createdAt");


    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/{id}/evaluations")
    @Operation(summary = "List evaluations for an interview (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<EvaluationResponse>>> listWithSummary(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id,
            @Valid @ModelAttribute PageRequestParams params) {

        Pageable pageable = PageRequestBuilder.build(
                params, ALLOWED_SORT, DEFAULT_SORT, "_id");

        return ResponseEntity.ok(ApiResponse.ok(
                evaluationService.listForInterviewPaged(user.getId(), id, pageable)));
    }

    @GetMapping("/{id}/evaluations/flat")
    @Operation(summary = "List flat evaluations for an interview")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> listFlat(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.listForInterview(user.getId(), id)));
    }
}
