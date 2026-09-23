package com.interviewai.mapper;

import com.interviewai.dto.interview.InterviewResponse;
import com.interviewai.dto.interview.InterviewSummaryResponse;
import com.interviewai.dto.interview.QuestionResponse;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;
import org.springframework.stereotype.Component;

@Component
public class InterviewMapper {

    public InterviewResponse toResponse(InterviewSession session) {
        if (session == null) return null;
        return InterviewResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .resumeId(session.getResumeId())
                .jobDescriptionId(session.getJobDescriptionId())
                .type(session.getType())
                .mode(session.getMode())
                .difficulty(session.getDifficulty())
                .personality(session.getPersonality())
                .status(session.getStatus())
                .state(session.getState())
                .questionIds(session.getQuestionIds())
                .focusTopic(session.getFocusTopic())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .durationSec(session.getDurationSec())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public InterviewSummaryResponse toSummary(InterviewSession session) {
        if (session == null) return null;
        var state = session.getState();
        return InterviewSummaryResponse.builder()
                .id(session.getId())
                .type(session.getType())
                .mode(session.getMode())
                .difficulty(session.getDifficulty())
                .personality(session.getPersonality())
                .status(session.getStatus())
                .totalQuestionsAsked(state != null ? state.getTotalQuestionsAsked() : 0)
                .cumulativeScore(state != null ? state.getCumulativeScore() : 0.0)
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .durationSec(session.getDurationSec())
                .build();
    }

    public QuestionResponse toQuestionResponse(Question question) {
        if (question == null) return null;
        return QuestionResponse.builder()
                .id(question.getId())
                .interviewId(question.getInterviewId())
                .order(question.getOrder())
                .type(question.getType())
                .text(question.getText())
                .expectedTopics(question.getExpectedTopics())
                .difficulty(question.getDifficulty())
                .generatedFrom(question.getGeneratedFrom())
                .parentQuestionId(question.getParentQuestionId())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
