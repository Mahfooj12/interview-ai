package com.interviewai.model.embedded;

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
public class ReportSections {

    private double overall;
    private double technical;
    private double communication;
    private double problemSolving;
    private double confidence;
    private double resumeKnowledge;
    private double projectKnowledge;
}
