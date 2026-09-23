package com.interviewai.mapper;

import com.interviewai.dto.evaluation.EvaluationResponse;
import com.interviewai.dto.evaluation.EvaluationSummaryResponse;
import com.interviewai.model.Evaluation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EvaluationMapper {

    public EvaluationResponse toResponse(Evaluation e) {
        if (e == null) return null;
        return EvaluationResponse.builder()
                .id(e.getId())
                .interviewId(e.getInterviewId())
                .questionId(e.getQuestionId())
                .answerId(e.getAnswerId())
                .userId(e.getUserId())
                .technicalAccuracy(e.getTechnicalAccuracy())
                .relevance(e.getRelevance())
                .depth(e.getDepth())
                .clarity(e.getClarity())
                .communication(e.getCommunication())
                .confidence(e.getConfidence())
                .overall(e.getOverall())
                .strengths(e.getStrengths())
                .weaknesses(e.getWeaknesses())
                .weakAreasDetected(e.getWeakAreasDetected())
                .feedback(e.getFeedback())
                .idealAnswer(e.getIdealAnswer())
                .followUpNeeded(e.isFollowUpNeeded())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public EvaluationSummaryResponse toSummary(List<Evaluation> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return EvaluationSummaryResponse.builder()
                    .totalEvaluations(0)
                    .build();
        }
        int n = evaluations.size();
        double technical = 0, communication = 0, confidence = 0, depth = 0, relevance = 0, overall = 0;
        for (Evaluation e : evaluations) {
            technical += e.getTechnicalAccuracy();
            communication += e.getCommunication();
            confidence += e.getConfidence();
            depth += e.getDepth();
            relevance += e.getRelevance();
            overall += e.getOverall();
        }
        return EvaluationSummaryResponse.builder()
                .averageTechnical(round(technical / n))
                .averageCommunication(round(communication / n))
                .averageConfidence(round(confidence / n))
                .averageDepth(round(depth / n))
                .averageRelevance(round(relevance / n))
                .averageOverall(round(overall / n))
                .totalEvaluations(n)
                .build();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}