package com.interviewai.service.impl;

import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;
import com.interviewai.service.AdaptiveEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdaptiveEngineImpl implements AdaptiveEngine {

    private static final double STRONG_THRESHOLD = 78.0;
    private static final double WEAK_THRESHOLD = 55.0;
    private static final int MAX_QUESTIONS = 15;

    @Value("${app.interview.max-questions:15}")
    private int maxQuestions;

    @Override
    public Decision decideNext(InterviewSession session, Evaluation lastEvaluation, List<Question> askedQuestions) {
        int asked = askedQuestions.size();

        // Hard stop
        if (asked >= Math.max(MAX_QUESTIONS, maxQuestions)) {
            return new Decision(false, true, "Maximum number of questions reached", session.getDifficulty());
        }

        InterviewSession.Difficulty next = nextDifficulty(session.getDifficulty(), lastEvaluation);

        // First question always goes through (no follow-up logic applies)
        if (lastEvaluation == null) {
            return new Decision(false, false, "Initial question", next);
        }

        boolean followUpWanted = lastEvaluation.isFollowUpNeeded()
                && lastEvaluation.getOverall() < STRONG_THRESHOLD
                && asked < maxQuestions - 1;

        if (followUpWanted) {
            return new Decision(true, false, "Evaluation suggested a follow-up", next);
        }

        // If overall score is very low for 3 consecutive questions, we don't end but
        // we mark as need-topic-change (handled by generator).
        return new Decision(false, false, "Proceed with new question", next);
    }

    @Override
    public void updateState(InterviewSession session, Evaluation evaluation) {
        var state = session.getState();
        if (state == null) return;

        state.setTotalQuestionsAsked(state.getTotalQuestionsAsked() + 1);
        state.setCurrentQuestionIndex(state.getTotalQuestionsAsked());
        state.setLastUpdatedAt(java.time.Instant.now());

        // Running average
        double prevTotal = state.getCumulativeScore() * (state.getTotalQuestionsAsked() - 1);
        double newAvg = (prevTotal + evaluation.getOverall()) / state.getTotalQuestionsAsked();
        state.setCumulativeScore(round(newAvg));

        // Strong/weak areas
        if (evaluation.getWeakAreasDetected() != null) {
            for (String area : evaluation.getWeakAreasDetected()) {
                if (area == null || area.isBlank()) continue;
                if (!state.getWeakAreas().contains(area)) {
                    state.getWeakAreas().add(area);
                }
            }
        }
        if (evaluation.getStrengths() != null) {
            for (String s : evaluation.getStrengths()) {
                if (s == null || s.isBlank()) continue;
                if (!state.getStrongAreas().contains(s)) {
                    state.getStrongAreas().add(s);
                }
            }
        }

        // Keep weak areas bounded
        if (state.getWeakAreas().size() > 12) {
            state.setWeakAreas(state.getWeakAreas().subList(0, 12));
        }
        if (state.getStrongAreas().size() > 12) {
            state.setStrongAreas(state.getStrongAreas().subList(0, 12));
        }

        session.setState(state);
    }

    @Override
    public InterviewSession.Difficulty nextDifficulty(InterviewSession.Difficulty current, Evaluation lastEvaluation) {
        if (lastEvaluation == null || current == null) {
            return current == null ? InterviewSession.Difficulty.INTERMEDIATE : current;
        }
        double score = lastEvaluation.getOverall();
        switch (current) {
            case BEGINNER -> {
                if (score >= STRONG_THRESHOLD) return InterviewSession.Difficulty.INTERMEDIATE;
            }
            case INTERMEDIATE -> {
                if (score >= STRONG_THRESHOLD) return InterviewSession.Difficulty.ADVANCED;
                if (score < WEAK_THRESHOLD) return InterviewSession.Difficulty.BEGINNER;
            }
            case ADVANCED -> {
                if (score < WEAK_THRESHOLD) return InterviewSession.Difficulty.INTERMEDIATE;
            }
        }
        return current;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}