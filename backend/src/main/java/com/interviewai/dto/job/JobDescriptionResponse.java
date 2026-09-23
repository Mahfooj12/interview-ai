package com.interviewai.dto.job;

import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
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
public class JobDescriptionResponse {
    private String id;
    private String userId;
    private String title;
    private String company;
    private String rawText;
    private String fileName;
    private String fileUrl;
    private ParsedJobDescription parsed;
    private MatchResult matchResult;
    private Instant createdAt;
    private Instant updatedAt;
}
