package com.interviewai.dto.resume;

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
public class ResumeUploadResponse {
    private String resumeId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private boolean parsed;
    private boolean analyzed;
    private String message;
}