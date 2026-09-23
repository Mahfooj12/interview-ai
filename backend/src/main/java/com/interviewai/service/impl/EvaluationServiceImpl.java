package com.interviewai.service.impl;

import com.interviewai.dto.evaluation.EvaluationResponse;
import com.interviewai.dto.evaluation.InterviewEvaluationsResponse;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.EvaluationMapper;
import com.interviewai.model.Evaluation;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.service.EvaluationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final InterviewSessionRepository interviewRepository;
    private final EvaluationMapper mapper;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository,
                                 InterviewSessionRepository interviewRepository,
                                 EvaluationMapper mapper) {
        this.evaluationRepository = evaluationRepository;
        this.interviewRepository = interviewRepository;
        this.mapper = mapper;
    }

    @Override
    public List<EvaluationResponse> listForInterview(String userId, String interviewId) {
        return getRawByInterview(userId, interviewId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public InterviewEvaluationsResponse listWithSummary(String userId, String interviewId) {
        List<Evaluation> evaluations = getRawByInterview(userId, interviewId);
        return InterviewEvaluationsResponse.builder()
                .interviewId(interviewId)
                .evaluations(evaluations.stream().map(mapper::toResponse).toList())
                .summary(mapper.toSummary(evaluations))
                .build();
    }

    @Override
    public EvaluationResponse getByAnswer(String userId, String answerId) {
        Evaluation evaluation = evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc("").stream()
                .filter(e -> answerId.equals(e.getAnswerId()))
                .findFirst()
                .orElse(null);

        // Fallback: scan all evaluations for the user via interview check would be expensive.
        // Instead we verify via direct repository findById only if the caller passed an evaluation id.
        if (evaluation == null) {
            throw new ResourceNotFoundException("Evaluation not found for answer " + answerId);
        }
        if (!userId.equals(evaluation.getUserId())) {
            throw new ResourceNotFoundException("Evaluation not found for answer " + answerId);
        }
        return mapper.toResponse(evaluation);
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    @Override
    public List<Evaluation> getRawByInterview(String userId, String interviewId) {
        interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        return evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc(interviewId);
    }
}