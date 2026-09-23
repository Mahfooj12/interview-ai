package com.interviewai.parser;

import com.interviewai.exception.BadRequestException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

@Component
public class DocxResumeParser implements ResumeParser {

    private static final String DOCX_MIME =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Override
    public boolean supports(String contentType) {
        return contentType != null && contentType.equalsIgnoreCase(DOCX_MIME);
    }

    @Override
    public String extractText(MultipartFile file) {
        try (InputStream in = file.getInputStream();
             XWPFDocument document = new XWPFDocument(in)) {
            String text = document.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining("\n"));
            if (text.isBlank()) {
                throw new BadRequestException("DOCX contains no extractable text");
            }
            return text.trim();
        } catch (IOException e) {
            throw new BadRequestException("Failed to read DOCX file: " + e.getMessage());
        }
    }
}