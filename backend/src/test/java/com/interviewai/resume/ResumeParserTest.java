package com.interviewai.resume;

import com.interviewai.exception.BadRequestException;
import com.interviewai.parser.DocxResumeParser;
import com.interviewai.parser.PdfResumeParser;
import com.interviewai.parser.ResumeParserFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumeParserTest {

    private final PdfResumeParser pdfParser = new PdfResumeParser();
    private final DocxResumeParser docxParser = new DocxResumeParser();
    private final ResumeParserFactory factory =
            new ResumeParserFactory(List.of(pdfParser, docxParser));

    @Test
    void pdfParser_extractsText() throws Exception {
        byte[] pdf = createPdf("Jane Doe\nJava, Spring Boot");
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", pdf);

        String text = pdfParser.extractText(file);

        assertThat(text).contains("Jane Doe");
        assertThat(text).contains("Java");
    }

    @Test
    void docxParser_extractsText() throws Exception {
        byte[] docx = createDocx("Jane Doe", "Java, Spring Boot");
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docx);

        String text = docxParser.extractText(file);

        assertThat(text).contains("Jane Doe");
        assertThat(text).contains("Java");
    }

    @Test
    void factory_resolvesByContentType() {
        assertThat(factory.getParser("application/pdf")).isInstanceOf(PdfResumeParser.class);
        assertThat(factory.getParser(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .isInstanceOf(DocxResumeParser.class);
    }

    @Test
    void factory_rejectsUnsupportedType() {
        assertThatThrownBy(() -> factory.getParser("text/plain"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void pdfParser_rejectsEmptyPdf() {
        byte[] empty = new byte[0];
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", empty);
        assertThatThrownBy(() -> pdfParser.extractText(file))
                .isInstanceOf(BadRequestException.class);
    }

    private byte[] createPdf(String content) throws Exception {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                for (String line : content.split("\n")) {
                    cs.showText(line);
                    cs.newLineAtOffset(0, -14);
                }
                cs.endText();
            }
            doc.save(out);
            return out.toByteArray();
        }
    }

    private byte[] createDocx(String... lines) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (String line : lines) {
                XWPFParagraph p = doc.createParagraph();
                p.createRun().setText(line);
            }
            doc.write(out);
            return out.toByteArray();
        }
    }
}
