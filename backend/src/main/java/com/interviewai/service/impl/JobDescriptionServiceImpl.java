package com.interviewai.service.impl;

import com.interviewai.ai.AIService;
import com.interviewai.dto.job.JobDescriptionRequest;
import com.interviewai.dto.job.JobDescriptionResponse;
import com.interviewai.dto.job.JobDescriptionSummaryResponse;
import com.interviewai.dto.job.JobMatchResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.JobDescriptionMapper;
import com.interviewai.model.AiUsage;
import com.interviewai.model.JobDescription;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.parser.ResumeParser;
import com.interviewai.parser.ResumeParserFactory;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.JobDescriptionRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.JobDescriptionService;
import com.interviewai.service.ResumeService;
import com.interviewai.storage.StorageService;
import com.interviewai.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class JobDescriptionServiceImpl implements JobDescriptionService {

    private static final Logger log = LoggerFactory.getLogger(JobDescriptionServiceImpl.class);

    private final JobDescriptionRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeService resumeService;
    private final AIService aiService;
    private final JobDescriptionMapper mapper;
    private final StorageService storageService;
    private final ResumeParserFactory parserFactory;
    private final AiUsageRepository aiUsageRepository;

    @Value("${app.file.max-size-bytes}")
    private long maxFileSizeBytes;

    public JobDescriptionServiceImpl(JobDescriptionRepository jobRepository,
                                     ResumeRepository resumeRepository,
                                     ResumeService resumeService,
                                     AIService aiService,
                                     JobDescriptionMapper mapper,
                                     StorageService storageService,
                                     ResumeParserFactory parserFactory,
                                     AiUsageRepository aiUsageRepository) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.resumeService = resumeService;
        this.aiService = aiService;
        this.mapper = mapper;
        this.storageService = storageService;
        this.parserFactory = parserFactory;
        this.aiUsageRepository = aiUsageRepository;
    }

    @Override
    public JobDescriptionResponse createFromText(String userId, JobDescriptionRequest request) {
        long start = System.currentTimeMillis();
        ParsedJobDescription parsed = aiService.analyzeJobDescription(userId, request.getRawText());
        recordUsage(userId, AiUsage.Operation.ANALYZE_JOB_DESCRIPTION, System.currentTimeMillis() - start);

        JobDescription jd = JobDescription.builder()
                .userId(userId)
                .title(defaultIfBlank(request.getTitle(), parsed.getTitle()))
                .company(defaultIfBlank(request.getCompany(), parsed.getCompany()))
                .rawText(request.getRawText())
                .parsed(parsed)
                .build();
        jd = jobRepository.save(jd);

        // Optional immediate match
        if (request.getResumeId() != null && !request.getResumeId().isBlank()) {
            MatchResult match = computeMatch(userId, jd, request.getResumeId());
            jd.setMatchResult(match);
            jd = jobRepository.save(jd);
        }

        log.info("Job description created id={} userId={}", jd.getId(), userId);
        return mapper.toResponse(jd);
    }

    @Override
    public JobDescriptionResponse createFromFile(String userId, MultipartFile file, String resumeId) {
        validateFile(file);

        String contentType = file.getContentType();
        ResumeParser parser = parserFactory.getParser(contentType);
        String rawText = parser.extractText(file);

        String fileUrl = storageService.store(file, "job-descriptions");

        long start = System.currentTimeMillis();
        ParsedJobDescription parsed = aiService.analyzeJobDescription(userId, rawText);
        recordUsage(userId, AiUsage.Operation.ANALYZE_JOB_DESCRIPTION, System.currentTimeMillis() - start);

        JobDescription jd = JobDescription.builder()
                .userId(userId)
                .title(parsed.getTitle())
                .company(parsed.getCompany())
                .rawText(rawText)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .parsed(parsed)
                .build();
        jd = jobRepository.save(jd);

        if (resumeId != null && !resumeId.isBlank()) {
            MatchResult match = computeMatch(userId, jd, resumeId);
            jd.setMatchResult(match);
            jd = jobRepository.save(jd);
        }

        log.info("Job description created from file id={} userId={}", jd.getId(), userId);
        return mapper.toResponse(jd);
    }

    @Override
    public List<JobDescriptionSummaryResponse> list(String userId) {
        return jobRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(mapper::toSummary)
                .toList();
    }

    @Override
    public JobDescriptionResponse get(String userId, String id) {
        return mapper.toResponse(getOwned(userId, id));
    }

    @Override
    public JobDescription getOwned(String userId, String id) {
        return jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found"));
    }

    @Override
    public JobMatchResponse match(String userId, String jobId, String resumeId) {
        JobDescription jd = getOwned(userId, jobId);
        MatchResult match = computeMatch(userId, jd, resumeId);
        jd.setMatchResult(match);
        jobRepository.save(jd);

        return JobMatchResponse.builder()
                .jobDescriptionId(jd.getId())
                .resumeId(resumeId)
                .matchResult(match)
                .message("Match computed")
                .build();
    }

    @Override
    public void delete(String userId, String id) {
        JobDescription jd = getOwned(userId, id);
        if (jd.getFileUrl() != null && !jd.getFileUrl().isBlank()) {
            try {
                storageService.delete(jd.getFileUrl());
            } catch (Exception e) {
                log.warn("Failed to delete stored JD file for {}: {}", id, e.getMessage());
            }
        }
        jobRepository.delete(jd);
    }

    // ------------------------------------------------------------------
    // INTERNAL
    // ------------------------------------------------------------------
    private MatchResult computeMatch(String userId, JobDescription jd, String resumeId) {
        Resume resume = resumeService.getOwned(userId, resumeId);
        ParsedResume parsedResume = resume.getParsed();

        if (parsedResume == null) {
            // Auto-analyze the resume first so matching works.
            long start = System.currentTimeMillis();
            parsedResume = aiService.analyzeResume(userId, resume.getRawText());
            recordUsage(userId, AiUsage.Operation.ANALYZE_RESUME, System.currentTimeMillis() - start);
            resume.setParsed(parsedResume);
            resumeRepository.save(resume);
        }

        long start = System.currentTimeMillis();
        MatchResult result = aiService.matchResumeWithJob(userId, parsedResume, jd.getParsed());
        recordUsage(userId, AiUsage.Operation.MATCH_RESUME_JD, System.currentTimeMillis() - start);
        return result;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Job description file is empty");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException("Job description file exceeds the maximum allowed size");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isAllowed(contentType)) {
            throw new BadRequestException("Job description file type is invalid. Allowed: PDF, DOCX");
        }
    }

    private boolean isAllowed(String contentType) {
        for (String allowed : Constants.ALLOWED_RESUME_MIME_TYPES) {
            if (allowed.equalsIgnoreCase(contentType)) return true;
        }
        return false;
    }

    private String defaultIfBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) return primary;
        return fallback;
    }

    private void recordUsage(String userId, AiUsage.Operation op, long latencyMs) {
        Map<String, Object> meta = aiService.lastUsageMetadata();
        AiUsage usage = AiUsage.builder()
                .userId(userId)
                .operation(op)
                .model(meta.getOrDefault("model", "unknown").toString())
                .promptTokens(intOf(meta.get("promptTokens")))
                .completionTokens(intOf(meta.get("completionTokens")))
                .totalTokens(intOf(meta.get("totalTokens")))
                .latencyMs(latencyMs)
                .success(true)
                .createdAt(Instant.now())
                .build();
        aiUsageRepository.save(usage);
    }

    private int intOf(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}