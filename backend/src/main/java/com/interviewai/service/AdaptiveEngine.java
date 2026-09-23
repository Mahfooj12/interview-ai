package com.interviewai.service;

import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;

import java.util.List;

/**
 * Decides the shape of the next step of an interview: whether to follow up,
 * escalate difficulty, or switch topic. Pure logic — no I/O.
 */
public interface AdaptiveEngine {

    /**
     * Decide what kind of next question to produce.
     */
    Decision decideNext(InterviewSession session, Evaluation lastEvaluation, List<Question> askedQuestions);

    /**
     * Recompute weak/strong areas and cumulative score after an evaluation.
     */
    void updateState(InterviewSession session, Evaluation evaluation);

    /**
     * Escalate or downgrade difficulty based on the last evaluation.
     */
    InterviewSession.Difficulty nextDifficulty(InterviewSession.Difficulty current, Evaluation lastEvaluation);

    record Decision(
            boolean followUp,
            boolean endInterview,
            String reason,
            InterviewSession.Difficulty nextDifficulty
    ) {}
}