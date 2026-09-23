package com.interviewai.controller;

import com.interviewai.dto.evaluation.EvaluationResponse;
import com.interviewai.dto.evaluation.InterviewEvaluationsResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@Tag(name = "Evaluations", description = "Per-answer AI evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/{id}/evaluations")
    @Operation(summary = "List evaluations with summary for an interview")
    public ResponseEntity<ApiResponse<InterviewEvaluationsResponse>> listWithSummary(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.listWithSummary(user.getId(), id)));
    }

    @GetMapping("/{id}/evaluations/flat")
    @Operation(summary = "List flat evaluations for an interview")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> listFlat(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.listForInterview(user.getId(), id)));
    }
}
