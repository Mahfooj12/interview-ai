package com.interviewai.report;

import com.interviewai.ai.AIService;
import com.interviewai.dto.report.ReportResponse;
import com.interviewai.mapper.ReportMapper;
import com.interviewai.model.Answer;
import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;
import com.interviewai.model.Report;
import com.interviewai.model.embedded.AnswerMetrics;
import com.interviewai.model.embedded.InterviewState;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.model.embedded.ReportSections;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.AnswerRepository;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.QuestionRepository;
import com.interviewai.repository.ReportRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private InterviewSessionRepository interviewRepository;
    @Mock private EvaluationRepository evaluationRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private AIService aiService;
    @Mock private AiUsageRepository aiUsageRepository;

    private ReportServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReportServiceImpl(
                reportRepository, interviewRepository, evaluationRepository,
                answerRepository, questionRepository, resumeRepository,
                aiService, new ReportMapper(), aiUsageRepository);
    }

    @Test
    void generate_persistsReportWithSections() {
        InterviewSession session = InterviewSession.builder()
                .id("i-1").userId("u-1").resumeId("r-1")
                .type(InterviewSession.Type.TECHNICAL)
                .difficulty(InterviewSession.Difficulty.INTERMEDIATE)
                .status(InterviewSession.Status.COMPLETED)
                .state(InterviewState.builder()
                        .weakAreas(new ArrayList<>()).strongAreas(new ArrayList<>()).build())
                .build();
        when(interviewRepository.findByIdAndUserId("i-1", "u-1")).thenReturn(Optional.of(session));
        when(reportRepository.findByInterviewId("i-1")).thenReturn(Optional.empty());

        Evaluation eval = Evaluation.builder()
                .id("e-1").interviewId("i-1").questionId("q-1").answerId("a-1")
                .overall(80).technicalAccuracy(80).communication(75).confidence(70)
                .createdAt(Instant.now()).build();
        when(evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc("i-1"))
                .thenReturn(List.of(eval));

        AIService.ReportResult ai = new AIService.ReportResult(
                82.0,
                ReportSections.builder().overall(82).technical(85).communication(72)
                        .problemSolving(80).confidence(76).resumeKnowledge(91).projectKnowledge(88).build(),
                List.of("Java"), List.of("System design"), List.of("Spring Security"), "Good job");
        when(aiService.generateFinalReport(anyString(), any(AIService.ReportContext.class))).thenReturn(ai);

        when(aiService.generateLearningPlan(anyString(), anyList(), anyString()))
                .thenReturn(List.of(LearningItem.builder().topic("Spring Security")
                        .priority("HIGH").resources(new ArrayList<>()).build()));
        when(aiService.lastUsageMetadata()).thenReturn(java.util.Map.of());

        Question q = Question.builder().id("q-1").text("What is DI?").build();
        when(questionRepository.findAllByInterviewIdOrderByOrderAsc("i-1")).thenReturn(List.of(q));
        Answer a = Answer.builder().id("a-1").questionId("q-1").text("DI is...")
                .metrics(AnswerMetrics.builder().wpm(120).clarityScore(80).build()).build();
        when(answerRepository.findAllByInterviewIdOrderByAnsweredAtAsc("i-1")).thenReturn(List.of(a));

        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0); r.setId("rep-1"); return r;
        });

        var generated = service.generate("u-1", "i-1", true);

        assertThat(generated.isGenerated()).isTrue();
        assertThat(generated.getReportId()).isEqualTo("rep-1");
    }

    @Test
    void get_returnsReport() {
        Report report = Report.builder()
                .id("rep-1").userId("u-1").interviewId("i-1")
                .sections(ReportSections.builder().overall(80).technical(85).build())
                .strongAreas(List.of("Java")).weakAreas(List.of("System design"))
                .questionFeedback(new ArrayList<>())
                .recommendedTopics(new ArrayList<>())
                .learningRoadmap(new ArrayList<>())
                .generatedAt(Instant.now()).build();
        when(reportRepository.findByIdAndUserId("rep-1", "u-1")).thenReturn(Optional.of(report));

        ReportResponse response = service.get("u-1", "rep-1");

        assertThat(response.getId()).isEqualTo("rep-1");
        assertThat(response.getSections().getOverall()).isEqualTo(80);
        assertThat(response.getStrongAreas()).containsExactly("Java");
    }
}
