package com.interviewai.job;

import com.interviewai.ai.AIService;
import com.interviewai.dto.job.JobDescriptionRequest;
import com.interviewai.dto.job.JobDescriptionResponse;
import com.interviewai.mapper.JobDescriptionMapper;
import com.interviewai.model.JobDescription;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.parser.ResumeParserFactory;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.JobDescriptionRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.ResumeService;
import com.interviewai.service.impl.JobDescriptionServiceImpl;
import com.interviewai.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobDescriptionServiceTest {

    @Mock private JobDescriptionRepository jobRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private ResumeService resumeService;
    @Mock private AIService aiService;
    @Mock private StorageService storageService;
    @Mock private ResumeParserFactory parserFactory;
    @Mock private AiUsageRepository aiUsageRepository;

    private JobDescriptionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new JobDescriptionServiceImpl(
                jobRepository, resumeRepository, resumeService, aiService,
                new JobDescriptionMapper(), storageService, parserFactory, aiUsageRepository);
        ReflectionTestUtils.setField(service, "maxFileSizeBytes", 10L * 1024 * 1024);
    }

    @Test
    void createFromText_parsesJdAndPersists() {
        ParsedJobDescription parsed = ParsedJobDescription.builder()
                .title("Backend Engineer")
                .company("Acme")
                .requiredSkills(List.of("Java", "Spring Boot"))
                .build();
        when(aiService.analyzeJobDescription(anyString(), anyString())).thenReturn(parsed);
        when(aiService.lastUsageMetadata()).thenReturn(java.util.Map.of());
        when(jobRepository.save(any(JobDescription.class))).thenAnswer(inv -> {
            JobDescription jd = inv.getArgument(0);
            jd.setId("jd-1");
            return jd;
        });

        JobDescriptionRequest req = JobDescriptionRequest.builder()
                .rawText("We need a backend engineer with Java and Spring Boot experience.")
                .build();

        JobDescriptionResponse response = service.createFromText("u-1", req);

        assertThat(response.getId()).isEqualTo("jd-1");
        assertThat(response.getParsed().getTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    void match_computesMatchAgainstResume() {
        ParsedJobDescription parsedJd = ParsedJobDescription.builder()
                .requiredSkills(List.of("Java")).build();
        JobDescription stored = JobDescription.builder()
                .id("jd-1").userId("u-1").parsed(parsedJd).build();
        when(jobRepository.findByIdAndUserId("jd-1", "u-1")).thenReturn(Optional.of(stored));
        when(jobRepository.save(any(JobDescription.class))).thenAnswer(inv -> inv.getArgument(0));

        ParsedResume parsedResume = ParsedResume.builder()
                .skills(List.of("Java", "Spring Boot")).build();
        Resume resume = Resume.builder().id("r-1").userId("u-1").parsed(parsedResume).build();
        when(resumeService.getOwned("u-1", "r-1")).thenReturn(resume);

        MatchResult match = MatchResult.builder()
                .matchedSkills(List.of("Java"))
                .missingSkills(List.of("Kubernetes"))
                .matchPercentage(70)
                .build();
        when(aiService.matchResumeWithJob(anyString(), any(ParsedResume.class), any(ParsedJobDescription.class)))
                .thenReturn(match);
        when(aiService.lastUsageMetadata()).thenReturn(java.util.Map.of());

        var response = service.match("u-1", "jd-1", "r-1");
        assertThat(response.getMatchResult().getMatchPercentage()).isEqualTo(70);
        assertThat(response.getMatchResult().getMissingSkills()).containsExactly("Kubernetes");
    }
}
