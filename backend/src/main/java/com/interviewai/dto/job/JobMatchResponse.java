package com.interviewai.dto.job;

import com.interviewai.model.embedded.MatchResult;
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
public class JobMatchResponse {
    private String jobDescriptionId;
    private String resumeId;
    private MatchResult matchResult;
    private String message;
}