package com.interviewai.service;

import com.interviewai.dto.report.GenerateReportResponse;
import com.interviewai.dto.report.ReportResponse;
import com.interviewai.dto.report.ReportSummaryResponse;

import java.util.List;

public interface ReportService {

    GenerateReportResponse generate(String userId, String interviewId, boolean force);

    ReportResponse get(String userId, String reportId);

    ReportResponse getByInterview(String userId, String interviewId);

    List<ReportSummaryResponse> list(String userId);
}
