package com.interviewai.service;

import com.interviewai.dto.job.JobDescriptionRequest;
import com.interviewai.dto.job.JobDescriptionResponse;
import com.interviewai.dto.job.JobDescriptionSummaryResponse;
import com.interviewai.dto.job.JobMatchResponse;
import com.interviewai.model.JobDescription;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobDescriptionService {

    JobDescriptionResponse createFromText(String userId, JobDescriptionRequest request);

    JobDescriptionResponse createFromFile(String userId, MultipartFile file, String resumeId);

    List<JobDescriptionSummaryResponse> list(String userId);

    JobDescriptionResponse get(String userId, String id);

    JobDescription getOwned(String userId, String id);

    JobMatchResponse match(String userId, String jobId, String resumeId);

    void delete(String userId, String id);
}
