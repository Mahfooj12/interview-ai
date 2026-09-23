package com.interviewai.evaluation;

import com.interviewai.analytics.impl.CommunicationAnalyzerImpl;
import com.interviewai.model.embedded.AnswerMetrics;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationAnalyzerImplTest {

    private final CommunicationAnalyzerImpl analyzer = new CommunicationAnalyzerImpl();

    @Test
    void detectsFillerWords() {
        AnswerMetrics m = analyzer.analyze(
                "Um, actually I basically worked on it, you know, like, a lot.", 10L);
        assertThat(m.getFillerWordCount()).isGreaterThanOrEqualTo(3);
        assertThat(m.getFillerWords()).contains("um", "actually", "basically", "like");
    }

    @Test
    void computesWpmFromDuration() {
        // 20 words in 30 seconds → 40 wpm
        String text = "one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty";
        AnswerMetrics m = analyzer.analyze(text, 30L);
        assertThat(m.getWpm()).isEqualTo(40.0);
    }

    @Test
    void emptyTextReturnsZeros() {
        AnswerMetrics m = analyzer.analyze("", null);
        assertThat(m.getWpm()).isEqualTo(0);
        assertThat(m.getFillerWordCount()).isEqualTo(0);
        assertThat(m.getClarityScore()).isEqualTo(0);
    }

    @Test
    void mergePrefersAudioValuesWhenPresent() {
        AnswerMetrics text = AnswerMetrics.builder()
                .wpm(120).fillerWordCount(4).hesitationCount(2)
                .clarityScore(70).confidenceScore(70).grammarScore(70).toneScore(70).build();
        AnswerMetrics audio = AnswerMetrics.builder()
                .wpm(150).fillerWordCount(6).hesitationCount(1)
                .clarityScore(80).confidenceScore(80).grammarScore(80).toneScore(80).build();

        AnswerMetrics merged = analyzer.merge(text, audio);

        assertThat(merged.getWpm()).isEqualTo(150);
        assertThat(merged.getFillerWordCount()).isEqualTo(6);
        assertThat(merged.getHesitationCount()).isEqualTo(2);
        assertThat(merged.getClarityScore()).isEqualTo(75.0);
    }

    @Test
    void grammarScorePenalizesMissingCapitalization() {
        AnswerMetrics m = analyzer.analyze("this is an answer without capitalization", null);
        assertThat(m.getGrammarScore()).isLessThan(100);
    }
}
