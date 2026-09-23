package com.interviewai.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescriptionSummaryResponse {
    private String id;
    private String title;
    private String company;
    private String seniority;
    private int requiredSkillCount;
    private Double matchPercentage;
    private Instant createdAt;
}