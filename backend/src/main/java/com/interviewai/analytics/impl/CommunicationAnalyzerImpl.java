package com.interviewai.analytics.impl;

import com.interviewai.analytics.CommunicationAnalyzer;
import com.interviewai.model.embedded.AnswerMetrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommunicationAnalyzerImpl implements CommunicationAnalyzer {

    private static final List<String> FILLERS = List.of(
            "um", "uh", "erm", "ah", "like", "actually", "basically",
            "you know", "i mean", "sort of", "kind of", "literally"
    );

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z']+");

    @Override
    public AnswerMetrics analyze(String answerText, Long elapsedSeconds) {
        if (answerText == null || answerText.isBlank()) {
            return AnswerMetrics.builder()
                    .wpm(0)
                    .fillerWordCount(0)
                    .fillerWords(new ArrayList<>())
                    .hesitationCount(0)
                    .clarityScore(0)
                    .confidenceScore(0)
                    .grammarScore(0)
                    .toneScore(0)
                    .build();
        }

        String normalized = answerText.toLowerCase(Locale.ROOT);

        // Filler words
        List<String> detected = new ArrayList<>();
        int fillerCount = 0;
        for (String filler : FILLERS) {
            int count = countOccurrences(normalized, filler);
            if (count > 0) {
                fillerCount += count;
                detected.add(filler);
            }
        }

        // Word count & WPM
        Matcher m = WORD_PATTERN.matcher(answerText);
        int wordCount = 0;
        while (m.find()) wordCount++;

        double wpm = 0;
        if (elapsedSeconds != null && elapsedSeconds > 0) {
            wpm = (wordCount / (double) elapsedSeconds) * 60.0;
        }

        // Hesitation — repeated punctuation, ellipses, repeated words
        int hesitation = countOccurrences(normalized, "...")
                + countRepeatedWords(normalized)
                + countOccurrences(normalized, "  ");

        // Scoring heuristics
        double clarity = clamp(100 - (fillerCount * 4) - (hesitation * 3));
        double confidence = clamp(60 + Math.min(wordCount, 250) * 0.12 - fillerCount * 3 - hesitation * 2);
        double grammar = basicGrammarScore(answerText);
        double tone = clamp(70 + Math.min(wordCount, 200) * 0.05 - fillerCount * 2);

        return AnswerMetrics.builder()
                .wpm(round(wpm))
                .fillerWordCount(fillerCount)
                .fillerWords(detected)
                .hesitationCount(hesitation)
                .clarityScore(round(clarity))
                .confidenceScore(round(confidence))
                .grammarScore(round(grammar))
                .toneScore(round(tone))
                .build();
    }

    @Override
    public AnswerMetrics merge(AnswerMetrics textMetrics, AnswerMetrics audioMetrics) {
        if (audioMetrics == null) return textMetrics;
        if (textMetrics == null) return audioMetrics;

        return AnswerMetrics.builder()
                .wpm(audioMetrics.getWpm() > 0 ? audioMetrics.getWpm() : textMetrics.getWpm())
                .fillerWordCount(Math.max(audioMetrics.getFillerWordCount(), textMetrics.getFillerWordCount()))
                .fillerWords(!audioMetrics.getFillerWords().isEmpty()
                        ? audioMetrics.getFillerWords() : textMetrics.getFillerWords())
                .hesitationCount(Math.max(audioMetrics.getHesitationCount(), textMetrics.getHesitationCount()))
                .clarityScore(averageNonZero(textMetrics.getClarityScore(), audioMetrics.getClarityScore()))
                .confidenceScore(averageNonZero(textMetrics.getConfidenceScore(), audioMetrics.getConfidenceScore()))
                .grammarScore(averageNonZero(textMetrics.getGrammarScore(), audioMetrics.getGrammarScore()))
                .toneScore(averageNonZero(textMetrics.getToneScore(), audioMetrics.getToneScore()))
                .build();
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------
    private int countOccurrences(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        int count = 0;
        int idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) != -1) {
            count++;
            idx += needle.length();
        }
        return count;
    }

    private int countRepeatedWords(String text) {
        String[] parts = text.split("\\s+");
        int repeats = 0;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase(parts[i - 1]) && !parts[i].isBlank()) repeats++;
        }
        return repeats;
    }

    private double basicGrammarScore(String text) {
        // Very lightweight heuristic: penalize missing capitalization and terminal punctuation
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return 0;
        double score = 100;

        if (!Character.isUpperCase(trimmed.charAt(0))) score -= 10;
        char last = trimmed.charAt(trimmed.length() - 1);
        if (last != '.' && last != '!' && last != '?') score -= 8;

        // ratio of very short sentences
        long sentences = text.chars().filter(c -> c == '.' || c == '!' || c == '?').count();
        if (sentences == 0 && trimmed.length() > 120) score -= 5;

        return clamp(score);
    }

    private double averageNonZero(double a, double b) {
        boolean aZero = a == 0;
        boolean bZero = b == 0;
        if (aZero && bZero) return 0;
        if (aZero) return b;
        if (bZero) return a;
        return (a + b) / 2.0;
    }

    private double clamp(double v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}