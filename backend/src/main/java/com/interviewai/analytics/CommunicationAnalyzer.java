package com.interviewai.analytics;

import com.interviewai.model.embedded.AnswerMetrics;

/**
 * Lightweight, provider-independent analyzer for textual answers.
 * Audio-derived metrics (WPM, tone) are produced by the voice pipeline and
 * merged with these text metrics.
 */
public interface CommunicationAnalyzer {

    /**
     * Analyze a text answer and return base metrics.
     *
     * @param answerText the candidate answer
     * @param elapsedSeconds duration the candidate took to answer (nullable)
     */
    AnswerMetrics analyze(String answerText, Long elapsedSeconds);

    /**
     * Merge text metrics with audio-provided metrics, preferring audio values
     * when present.
     */
    AnswerMetrics merge(AnswerMetrics textMetrics, AnswerMetrics audioMetrics);
}
