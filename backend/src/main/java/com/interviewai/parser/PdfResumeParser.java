package com.interviewai.parser;

import com.interviewai.exception.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfResumeParser implements ResumeParser {

    @Override
    public boolean supports(String contentType) {
        return contentType != null && contentType.equalsIgnoreCase("application/pdf");
    }

    @Override
    public String extractText(MultipartFile file) {
        try (InputStream in = file.getInputStream();
             PDDocument document = Loader.loadPDF(in.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            if (text == null || text.isBlank()) {
                throw new BadRequestException("PDF contains no extractable text");
            }
            return text.trim();
        } catch (IOException e) {
            throw new BadRequestException("Failed to read PDF file: " + e.getMessage());
        }
    }
}
