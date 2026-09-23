package com.interviewai.dto.resume;

import com.interviewai.model.embedded.ParsedResume;
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
public class ResumeAnalysisResponse {
    private String resumeId;
    private boolean analyzed;
    private ParsedResume parsed;
    private String message;
}