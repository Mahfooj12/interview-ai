package com.interviewai.dto.resume;

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
public class ResumeSummaryResponse {
    private String id;
    private String fileName;
    private String fileType;
    private boolean primaryResume;
    private String parsedName;
    private int skillCount;
    private int projectCount;
    private Instant createdAt;
}