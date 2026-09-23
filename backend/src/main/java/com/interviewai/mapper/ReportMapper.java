package com.interviewai.mapper;

import com.interviewai.dto.report.QuestionFeedbackResponse;
import com.interviewai.dto.report.ReportResponse;
import com.interviewai.dto.report.ReportSummaryResponse;
import com.interviewai.model.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(Report report) {
        if (report == null) return null;
        return ReportResponse.builder()
                .id(report.getId())
                .interviewId(report.getInterviewId())
                .userId(report.getUserId())
                .sections(report.getSections())
                .strongAreas(report.getStrongAreas())
                .weakAreas(report.getWeakAreas())
                .questionFeedback(report.getQuestionFeedback() == null ? null
                        : report.getQuestionFeedback().stream()
                                .map(qf -> QuestionFeedbackResponse.builder()
                                        .questionId(qf.getQuestionId())
                                        .question(qf.getQuestion())
                                        .answer(qf.getAnswer())
                                        .score(qf.getScore())
                                        .feedback(qf.getFeedback())
                                        .idealAnswer(qf.getIdealAnswer())
                                        .build())
                                .toList())
                .recommendedTopics(report.getRecommendedTopics())
                .learningRoadmap(report.getLearningRoadmap())
                .communicationStats(report.getCommunicationStats())
                .summary(report.getSummary())
                .generatedAt(report.getGeneratedAt())
                .build();
    }

    public ReportSummaryResponse toSummary(Report report) {
        if (report == null) return null;
        var s = report.getSections();
        return ReportSummaryResponse.builder()
                .id(report.getId())
                .interviewId(report.getInterviewId())
                .overallScore(s != null ? s.getOverall() : 0)
                .technical(s != null ? s.getTechnical() : 0)
                .communication(s != null ? s.getCommunication() : 0)
                .confidence(s != null ? s.getConfidence() : 0)
                .problemSolving(s != null ? s.getProblemSolving() : 0)
                .generatedAt(report.getGeneratedAt())
                .build();
    }
}
