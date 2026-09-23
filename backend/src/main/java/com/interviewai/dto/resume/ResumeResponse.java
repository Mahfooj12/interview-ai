package com.interviewai.dto.resume;

import com.interviewai.model.embedded.ParsedResume;
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
public class ResumeResponse {
    private String id;
    private String userId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private long fileSize;
    private ParsedResume parsed;
    private boolean primaryResume;
    private int version;
    private Instant createdAt;
    private Instant updatedAt;
}