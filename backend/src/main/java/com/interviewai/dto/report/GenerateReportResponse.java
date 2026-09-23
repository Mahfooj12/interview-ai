package com.interviewai.dto.report;

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
public class GenerateReportResponse {
    private String reportId;
    private String interviewId;
    private boolean generated;
    private String message;
}
