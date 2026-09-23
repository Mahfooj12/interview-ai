package com.interviewai.controller;

import com.interviewai.dto.interview.AnswerRequest;
import com.interviewai.dto.interview.AnswerResponse;
import com.interviewai.dto.interview.CreateInterviewRequest;
import com.interviewai.dto.interview.InterviewResponse;
import com.interviewai.dto.interview.InterviewStateResponse;
import com.interviewai.dto.interview.InterviewSummaryResponse;
import com.interviewai.dto.interview.NextQuestionResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@Tag(name = "Interviews", description = "Adaptive AI interview engine")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    @Operation(summary = "Create a new interview session")
    public ResponseEntity<ApiResponse<InterviewResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateInterviewRequest request) {
        InterviewResponse response = interviewService.create(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Interview created"));
    }

    @GetMapping
    @Operation(summary = "List the current user's interviews")
    public ResponseEntity<ApiResponse<List<InterviewSummaryResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.list(user.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an interview by id")
    public ResponseEntity<ApiResponse<InterviewResponse>> get(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.get(user.getId(), id)));
    }

    @GetMapping("/{id}/state")
    @Operation(summary = "Get the adaptive state of an interview")
    public ResponseEntity<ApiResponse<InterviewStateResponse>> state(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.getState(user.getId(), id)));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start an interview and receive the first question")
    public ResponseEntity<ApiResponse<NextQuestionResponse>> start(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        NextQuestionResponse response = interviewService.start(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Interview started"));
    }

    @PostMapping("/{id}/answer")
    @Operation(summary = "Submit an answer and receive the next question")
    public ResponseEntity<ApiResponse<AnswerResponse>> answer(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id,
            @Valid @RequestBody AnswerRequest request) {
        AnswerResponse response = interviewService.submitAnswer(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Answer submitted"));
    }

    @PostMapping("/{id}/next-question")
    @Operation(summary = "Request the next question")
    public ResponseEntity<ApiResponse<NextQuestionResponse>> nextQuestion(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        NextQuestionResponse response = interviewService.nextQuestion(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Next question"));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete an interview")
    public ResponseEntity<ApiResponse<InterviewStateResponse>> complete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String id) {
        InterviewStateResponse response = interviewService.complete(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Interview completed"));
    }
}