package com.interviewai.dto.admin;

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
public class AdminStatisticsResponse {

    private long totalUsers;
    private long activeUsers;
    private long suspendedUsers;

    private long totalInterviews;
    private long completedInterviews;
    private long inProgressInterviews;

    private long totalReports;

    private long totalAiCalls24h;
    private long totalAiCalls7d;
    private long totalAiCalls30d;

    private long totalTokens30d;
    private double averageLatencyMs;

    @Builder.Default private List<OperationCount> topOperations = new ArrayList<>();

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OperationCount {
        private String operation;
        private long count;
    }
}
