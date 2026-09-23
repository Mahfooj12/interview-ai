package com.interviewai.service;

import com.interviewai.dto.evaluation.EvaluationResponse;
import com.interviewai.dto.evaluation.InterviewEvaluationsResponse;
import com.interviewai.model.Evaluation;

import java.util.List;

public interface EvaluationService {

    List<EvaluationResponse> listForInterview(String userId, String interviewId);

    InterviewEvaluationsResponse listWithSummary(String userId, String interviewId);

    EvaluationResponse getByAnswer(String userId, String answerId);

    Evaluation save(Evaluation evaluation);

    List<Evaluation> getRawByInterview(String userId, String interviewId);
}
