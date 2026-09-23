package com.interviewai.mapper;

import com.interviewai.dto.resume.ResumeResponse;
import com.interviewai.dto.resume.ResumeSummaryResponse;
import com.interviewai.model.Resume;
import org.springframework.stereotype.Component;

// import java.util.ArrayList;
// import java.util.Objects;

@Component
public class ResumeMapper {

    public ResumeResponse toResponse(Resume resume) {
        if (resume == null) return null;
        return ResumeResponse.builder()
                .id(resume.getId())
                .userId(resume.getUserId())
                .fileName(resume.getFileName())
                .fileUrl(resume.getFileUrl())
                .fileType(resume.getFileType())
                .fileSize(resume.getFileSize())
                .parsed(resume.getParsed())
                .primaryResume(resume.isPrimaryResume())
                .version(resume.getVersion())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }

    public ResumeSummaryResponse toSummary(Resume resume) {
        if (resume == null) return null;
        var parsed = resume.getParsed();
        return ResumeSummaryResponse.builder()
                .id(resume.getId())
                .fileName(resume.getFileName())
                .fileType(resume.getFileType())
                .primaryResume(resume.isPrimaryResume())
                .parsedName(parsed != null ? parsed.getName() : null)
                .skillCount(parsed != null && parsed.getSkills() != null ? parsed.getSkills().size() : 0)
                .projectCount(parsed != null && parsed.getProjects() != null ? parsed.getProjects().size() : 0)
                .createdAt(resume.getCreatedAt())
                .build();
    }
}