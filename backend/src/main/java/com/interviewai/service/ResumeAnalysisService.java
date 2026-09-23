package com.interviewai.service;

import com.interviewai.dto.resume.ResumeAnalysisResponse;

public interface ResumeAnalysisService {

    /**
     * Analyze the given resume using AI and persist the parsed structure.
     * If the resume was already analyzed and force=false, the existing parsed
     * result is returned.
     */
    ResumeAnalysisResponse analyze(String userId, String resumeId, boolean force);
}
