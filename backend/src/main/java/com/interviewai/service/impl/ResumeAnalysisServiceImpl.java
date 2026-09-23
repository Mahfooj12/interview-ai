package com.interviewai.service.impl;

import com.interviewai.ai.AIService;
import com.interviewai.dto.resume.ResumeAnalysisResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.model.AiUsage;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.ResumeAnalysisService;
import com.interviewai.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ResumeAnalysisServiceImpl.class);

    private final ResumeService resumeService;
    private final ResumeRepository resumeRepository;
    private final AIService aiService;
    private final AiUsageRepository aiUsageRepository;

    public ResumeAnalysisServiceImpl(ResumeService resumeService,
                                     ResumeRepository resumeRepository,
                                     AIService aiService,
                                     AiUsageRepository aiUsageRepository) {
        this.resumeService = resumeService;
        this.resumeRepository = resumeRepository;
        this.aiService = aiService;
        this.aiUsageRepository = aiUsageRepository;
    }

    @Override
    public ResumeAnalysisResponse analyze(String userId, String resumeId, boolean force) {
        Resume resume = resumeService.getOwned(userId, resumeId);

        if (!force && resume.getParsed() != null) {
            return ResumeAnalysisResponse.builder()
                    .resumeId(resume.getId())
                    .analyzed(true)
                    .parsed(resume.getParsed())
                    .message("Resume already analyzed")
                    .build();
        }

        if (resume.getRawText() == null || resume.getRawText().isBlank()) {
            throw new BadRequestException("Resume has no extractable text to analyze");
        }

        long start = System.currentTimeMillis();
        ParsedResume parsed = aiService.analyzeResume(userId, resume.getRawText());

        resume.setParsed(parsed);
        resumeRepository.save(resume);

        recordUsage(userId, AiUsage.Operation.ANALYZE_RESUME, System.currentTimeMillis() - start, true, null);
        log.info("Resume {} analyzed for user {}", resumeId, userId);

        return ResumeAnalysisResponse.builder()
                .resumeId(resume.getId())
                .analyzed(true)
                .parsed(parsed)
                .message("Resume analyzed successfully")
                .build();
    }

    private void recordUsage(String userId, AiUsage.Operation op, long latencyMs, boolean success, String error) {
        Map<String, Object> meta = aiService.lastUsageMetadata();
        AiUsage usage = AiUsage.builder()
                .userId(userId)
                .operation(op)
                .model(meta.getOrDefault("model", "unknown").toString())
                .promptTokens(intOf(meta.get("promptTokens")))
                .completionTokens(intOf(meta.get("completionTokens")))
                .totalTokens(intOf(meta.get("totalTokens")))
                .latencyMs(latencyMs)
                .success(success)
                .errorMessage(error)
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