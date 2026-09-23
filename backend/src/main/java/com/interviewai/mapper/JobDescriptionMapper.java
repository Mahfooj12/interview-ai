package com.interviewai.mapper;

import com.interviewai.dto.job.JobDescriptionResponse;
import com.interviewai.dto.job.JobDescriptionSummaryResponse;
import com.interviewai.model.JobDescription;
import org.springframework.stereotype.Component;

@Component
public class JobDescriptionMapper {

    public JobDescriptionResponse toResponse(JobDescription jd) {
        if (jd == null) return null;
        return JobDescriptionResponse.builder()
                .id(jd.getId())
                .userId(jd.getUserId())
                .title(jd.getTitle())
                .company(jd.getCompany())
                .rawText(jd.getRawText())
                .fileName(jd.getFileName())
                .fileUrl(jd.getFileUrl())
                .parsed(jd.getParsed())
                .matchResult(jd.getMatchResult())
                .createdAt(jd.getCreatedAt())
                .updatedAt(jd.getUpdatedAt())
                .build();
    }

    public JobDescriptionSummaryResponse toSummary(JobDescription jd) {
        if (jd == null) return null;
        var parsed = jd.getParsed();
        return JobDescriptionSummaryResponse.builder()
                .id(jd.getId())
                .title(jd.getTitle())
                .company(jd.getCompany())
                .seniority(parsed != null ? parsed.getSeniority() : null)
                .requiredSkillCount(parsed != null && parsed.getRequiredSkills() != null
                        ? parsed.getRequiredSkills().size() : 0)
                .matchPercentage(jd.getMatchResult() != null
                        ? jd.getMatchResult().getMatchPercentage() : null)
                .createdAt(jd.getCreatedAt())
                .build();
    }
}
