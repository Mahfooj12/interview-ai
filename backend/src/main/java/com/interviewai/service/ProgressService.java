package com.interviewai.service;

import com.interviewai.dto.progress.ProgressSkillResponse;
import com.interviewai.dto.progress.ProgressSummaryResponse;

import java.util.List;

public interface ProgressService {

    ProgressSummaryResponse summary(String userId);

    List<ProgressSkillResponse> skills(String userId);
}