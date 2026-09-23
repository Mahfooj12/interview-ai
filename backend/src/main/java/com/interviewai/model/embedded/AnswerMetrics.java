package com.interviewai.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerMetrics {

    private double wpm;
    private int fillerWordCount;
    @Builder.Default private List<String> fillerWords = new ArrayList<>();
    private int hesitationCount;
    private double clarityScore;
    private double confidenceScore;
    private double grammarScore;
    private double toneScore;
}
