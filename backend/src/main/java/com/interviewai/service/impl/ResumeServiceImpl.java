package com.interviewai.service.impl;

import com.interviewai.dto.resume.ResumeResponse;
import com.interviewai.dto.resume.ResumeSummaryResponse;
import com.interviewai.dto.resume.ResumeUploadResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.ResumeMapper;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.parser.ResumeParser;
import com.interviewai.parser.ResumeParserFactory;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.ResumeService;
import com.interviewai.storage.StorageService;
import com.interviewai.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import java.io.IOException;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final ResumeRepository resumeRepository;
    private final StorageService storageService;
    private final ResumeParserFactory parserFactory;
    private final ResumeMapper resumeMapper;

    @Value("${app.file.max-size-bytes}")
    private long maxFileSizeBytes;

    public ResumeServiceImpl(ResumeRepository resumeRepository,
                             StorageService storageService,
                             ResumeParserFactory parserFactory,
                             ResumeMapper resumeMapper) {
        this.resumeRepository = resumeRepository;
        this.storageService = storageService;
        this.parserFactory = parserFactory;
        this.resumeMapper = resumeMapper;
    }

    @Override
    public ResumeUploadResponse upload(String userId, MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();
        ResumeParser parser = parserFactory.getParser(contentType);

        // Extract text first — fail fast before uploading to storage.
        String rawText = parser.extractText(file);

        String fileUrl = storageService.store(file, "resumes");

        long count = resumeRepository.countByUserId(userId);
        boolean makePrimary = count == 0;

        Resume resume = Resume.builder()
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(detectType(contentType))
                .fileSize(file.getSize())
                .rawText(rawText)
                .primaryResume(makePrimary)
                .version(1)
                .parsed(null) // populated in Phase 5 by AIService
                .build();

        resume = resumeRepository.save(resume);
        log.info("Resume uploaded id={} userId={} size={}", resume.getId(), userId, file.getSize());

        return ResumeUploadResponse.builder()
                .resumeId(resume.getId())
                .fileName(resume.getFileName())
                .fileUrl(resume.getFileUrl())
                .fileType(resume.getFileType())
                .parsed(false)
                .analyzed(false)
                .message("Resume uploaded. AI analysis pending.")
                .build();
    }

    @Override
    public List<ResumeSummaryResponse> list(String userId) {
        return resumeRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(resumeMapper::toSummary)
                .toList();
    }

    @Override
    public ResumeResponse get(String userId, String resumeId) {
        Resume resume = getOwned(userId, resumeId);
        return resumeMapper.toResponse(resume);
    }

    @Override
    public Resume getOwned(String userId, String resumeId) {
        return resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
    }

    @Override
    public void delete(String userId, String resumeId) {
        Resume resume = getOwned(userId, resumeId);
        try {
            storageService.delete(resume.getFileUrl());
        } catch (Exception e) {
            log.warn("Failed to delete stored file for resume {}: {}", resumeId, e.getMessage());
        }
        resumeRepository.delete(resume);

        // If the deleted resume was primary, promote the most recent remaining one.
        if (resume.isPrimaryResume()) {
            resumeRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                    .findFirst()
                    .ifPresent(r -> {
                        r.setPrimaryResume(true);
                        resumeRepository.save(r);
                    });
        }
    }

    @Override
    public ResumeResponse setPrimary(String userId, String resumeId) {
        Resume target = getOwned(userId, resumeId);
        resumeRepository.findAllByUserIdOrderByCreatedAtDesc(userId).forEach(r -> {
            boolean shouldBePrimary = r.getId().equals(target.getId());
            if (r.isPrimaryResume() != shouldBePrimary) {
                r.setPrimaryResume(shouldBePrimary);
                resumeRepository.save(r);
            }
        });
        target.setPrimaryResume(true);
        return resumeMapper.toResponse(target);
    }

    @Override
    public Resume getPrimaryOrThrow(String userId) {
        return resumeRepository.findFirstByUserIdAndPrimaryResumeTrue(userId)
                .or(() -> resumeRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().findFirst())
                .orElseThrow(() -> new ResourceNotFoundException("No resume found for user"));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file is empty");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException("Resume file exceeds the maximum allowed size");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isAllowed(contentType)) {
            throw new BadRequestException("Resume file type is invalid. Allowed: PDF, DOCX");
        }
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Resume file name is missing");
        }
    }

    private boolean isAllowed(String contentType) {
        for (String allowed : Constants.ALLOWED_RESUME_MIME_TYPES) {
            if (allowed.equalsIgnoreCase(contentType)) return true;
        }
        return false;
    }

    private String detectType(String contentType) {
        if (contentType.equalsIgnoreCase("application/pdf")) return "PDF";
        return "DOCX";
    }

    /** Utility used by Phase 5 AIService to persist parsed results. */
    public Resume attachParsedResume(String userId, String resumeId, ParsedResume parsed) {
        Resume resume = getOwned(userId, resumeId);
        resume.setParsed(parsed);
        return resumeRepository.save(resume);
    }
}
