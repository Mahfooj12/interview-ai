package com.interviewai.resume;

import com.interviewai.dto.resume.ResumeUploadResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.ResumeMapper;
import com.interviewai.model.Resume;
import com.interviewai.parser.DocxResumeParser;
import com.interviewai.parser.PdfResumeParser;
import com.interviewai.parser.ResumeParserFactory;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.impl.ResumeServiceImpl;
import com.interviewai.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock private ResumeRepository resumeRepository;
    @Mock private StorageService storageService;

    private ResumeParserFactory parserFactory;
    private ResumeMapper resumeMapper;
    private ResumeServiceImpl service;

    @BeforeEach
    void setUp() {
        parserFactory = new ResumeParserFactory(List.of(new PdfResumeParser(), new DocxResumeParser()));
        resumeMapper = new ResumeMapper();
        service = new ResumeServiceImpl(resumeRepository, storageService, parserFactory, resumeMapper);
        ReflectionTestUtils.setField(service, "maxFileSizeBytes", 10L * 1024 * 1024);
    }

    @Test
    void upload_rejectsEmptyFile() {
        MockMultipartFile empty = new MockMultipartFile("file", "x.pdf", "application/pdf", new byte[0]);
        assertThatThrownBy(() -> service.upload("u-1", empty))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void upload_rejectsUnsupportedMimeType() {
        MockMultipartFile file = new MockMultipartFile("file", "x.txt", "text/plain", "text".getBytes());
        assertThatThrownBy(() -> service.upload("u-1", file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void upload_storesFileAndPersists() {
        byte[] pdf = minimalPdf();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdf);

        when(storageService.store(any(), anyString())).thenReturn("https://cdn/resume.pdf");
        when(resumeRepository.countByUserId("u-1")).thenReturn(0L);
        when(resumeRepository.save(any(Resume.class))).thenAnswer(inv -> {
            Resume r = inv.getArgument(0);
            r.setId("r-1");
            return r;
        });

        ResumeUploadResponse response = service.upload("u-1", file);

        assertThat(response.getResumeId()).isEqualTo("r-1");
        assertThat(response.getFileUrl()).isEqualTo("https://cdn/resume.pdf");
        assertThat(response.isParsed()).isFalse();
        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    void getOwned_throwsWhenMissing() {
        when(resumeRepository.findByIdAndUserId("r-1", "u-1")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getOwned("u-1", "r-1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private byte[] minimalPdf() {
        // Reuse the parser test PDF generator logic in a compact form.
        try {
            org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument();
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            doc.addPage(page);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs =
                         new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new org.apache.pdfbox.pdmodel.font.PDType1Font(
                        org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                cs.showText("Jane Doe - Java");
                cs.endText();
            }
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            doc.save(out);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
