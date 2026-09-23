package com.interviewai.parser;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeParser {

    /**
     * Whether this parser supports the given MIME type.
     */
    boolean supports(String contentType);

    /**
     * Extract raw plain text from the resume file.
     */
    String extractText(MultipartFile file);
}