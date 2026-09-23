package com.interviewai.service;

import com.interviewai.dto.resume.ResumeResponse;
import com.interviewai.dto.resume.ResumeSummaryResponse;
import com.interviewai.dto.resume.ResumeUploadResponse;
import com.interviewai.model.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    ResumeUploadResponse upload(String userId, MultipartFile file);

    List<ResumeSummaryResponse> list(String userId);

    ResumeResponse get(String userId, String resumeId);

    Resume getOwned(String userId, String resumeId);

    void delete(String userId, String resumeId);

    ResumeResponse setPrimary(String userId, String resumeId);

    Resume getPrimaryOrThrow(String userId);
}