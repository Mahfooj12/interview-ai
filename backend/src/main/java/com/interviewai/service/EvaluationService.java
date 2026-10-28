package com.interviewai.service;

import com.interviewai.dto.common.PageResponse;
import com.interviewai.dto.evaluation.EvaluationResponse;
import com.interviewai.dto.evaluation.InterviewEvaluationsResponse;
import com.interviewai.model.Evaluation;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EvaluationService {

    List<EvaluationResponse> listForInterview(String userId, String interviewId);

    InterviewEvaluationsResponse listWithSummary(String userId, String interviewId);

    // NEW
    PageResponse<EvaluationResponse> listForInterviewPaged(
            String userId, String interviewId, Pageable pageable);

    EvaluationResponse getByAnswer(String userId, String answerId);

    Evaluation save(Evaluation evaluation);

    List<Evaluation> getRawByInterview(String userId, String interviewId);
}