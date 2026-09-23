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
public class MatchResult {

    @Builder.Default private List<String> matchedSkills = new ArrayList<>();
    @Builder.Default private List<String> partiallyMatchedSkills = new ArrayList<>();
    @Builder.Default private List<String> missingSkills = new ArrayList<>();

    private double matchPercentage;
}