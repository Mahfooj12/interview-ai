package com.interviewai.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressSkillResponse {
    private String skill;
    private int count;
    private double averageScore;
}