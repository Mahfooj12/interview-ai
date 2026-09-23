package com.interviewai.parser;

import com.interviewai.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeParserFactory {

    private final List<ResumeParser> parsers;

    public ResumeParserFactory(List<ResumeParser> parsers) {
        this.parsers = parsers;
    }

    public ResumeParser getParser(String contentType) {
        return parsers.stream()
                .filter(p -> p.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Unsupported resume file type: " + contentType));
    }
}
