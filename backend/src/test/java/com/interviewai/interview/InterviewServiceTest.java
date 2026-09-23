package com.interviewai.interview;

import com.interviewai.ai.AIService;
import com.interviewai.analytics.CommunicationAnalyzer;
import com.interviewai.dto.interview.AnswerRequest;
import com.interviewai.dto.interview.AnswerResponse;
import com.interviewai.dto.interview.CreateInterviewRequest;
import com.interviewai.dto.interview.InterviewResponse;
//import com.interviewai.dto.interview.NextQuestionResponse;
import com.interviewai.mapper.InterviewMapper;
import com.interviewai.model.Answer;
import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.InterviewState;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.AnswerRepository;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.JobDescriptionRepository;
import com.interviewai.repository.QuestionRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.AdaptiveEngine;
//import com.interviewai.service.InterviewService;
import com.interviewai.service.ReportService;
import com.interviewai.service.impl.InterviewServiceImpl;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock private InterviewSessionRepository interviewRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private EvaluationRepository evaluationRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private JobDescriptionRepository jobRepository;
    @Mock private AIService aiService;
    @Mock private AdaptiveEngine adaptiveEngine;
    @Mock private AiUsageRepository aiUsageRepository;
    @Mock private CommunicationAnalyzer communicationAnalyzer;
    @Mock private ReportService reportService;

    private InterviewServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InterviewServiceImpl(
                interviewRepository, questionRepository, answerRepository,
                evaluationRepository, resumeRepository, jobRepository,
                aiService, adaptiveEngine, new InterviewMapper(),
                aiUsageRepository, communicationAnalyzer, reportService);
    }

    @Test
    void create_withoutResume_throws() {
        when(resumeRepository.findFirstByUserIdAndPrimaryResumeTrue("u-1"))
                .thenReturn(Optional.empty());
        when(resumeRepository.findAllByUserIdOrderByCreatedAtDesc("u-1"))
                .thenReturn(List.of());

        CreateInterviewRequest req = CreateInterviewRequest.builder()
                .type(InterviewSession.Type.TECHNICAL)
                .mode(InterviewSession.Mode.TEXT)
                .difficulty(InterviewSession.Difficulty.INTERMEDIATE)
                .personality(InterviewSession.Personality.PROFESSIONAL)
                .build();

        assertThatThrownBy(() -> service.create("u-1", req))
                .hasMessageContaining("No resume");
    }

    @Test
    void create_withPrimaryResume_succeeds() {
        Resume resume = Resume.builder()
                .id("r-1").userId("u-1").primaryResume(true)
                .parsed(ParsedResume.builder().skills(new ArrayList<>()).build())
                .build();
        when(resumeRepository.findFirstByUserIdAndPrimaryResumeTrue("u-1"))
                .thenReturn(Optional.of(resume));
        when(interviewRepository.save(any(InterviewSession.class))).thenAnswer(inv -> {
            InterviewSession s = inv.getArgument(0);
            s.setId("i-1");
            return s;
        });

        CreateInterviewRequest req = CreateInterviewRequest.builder()
                .type(InterviewSession.Type.TECHNICAL)
                .mode(InterviewSession.Mode.TEXT)
                .difficulty(InterviewSession.Difficulty.INTERMEDIATE)
                .personality(InterviewSession.Personality.PROFESSIONAL)
                .build();

        InterviewResponse response = service.create("u-1", req);
        assertThat(response.getId()).isEqualTo("i-1");
        assertThat(response.getStatus()).isEqualTo(InterviewSession.Status.SCHEDULED);
    }

    @Test
    void submitAnswer_persistsAndReturnsEvaluation() {
        InterviewSession session = inProgressSession();
        when(interviewRepository.findByIdAndUserId("i-1", "u-1")).thenReturn(Optional.of(session));

        Question question = Question.builder()
                .id("q-1").interviewId("i-1").order(1).text("What is DI?")
                .expectedTopics(List.of("Dependency injection")).build();
        when(questionRepository.findById("q-1")).thenReturn(Optional.of(question));

        when(communicationAnalyzer.analyze(anyString(), any()))
                .thenReturn(com.interviewai.model.embedded.AnswerMetrics.builder().build());
        when(answerRepository.save(any(Answer.class))).thenAnswer(inv -> {
            Answer a = inv.getArgument(0); a.setId("a-1"); return a;
        });

        Evaluation eval = Evaluation.builder()
                .overall(80).followUpNeeded(false)
                .technicalAccuracy(80).relevance(80).depth(80).clarity(80)
                .communication(80).confidence(80)
                .strengths(new ArrayList<>()).weaknesses(new ArrayList<>())
                .weakAreasDetected(new ArrayList<>()).build();
        when(aiService.evaluateAnswer(anyString(), any(AIService.EvaluationContext.class))).thenReturn(eval);
        when(aiService.lastUsageMetadata()).thenReturn(java.util.Map.of());
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(inv -> inv.getArgument(0));

        when(adaptiveEngine.decideNext(any(), any(), any())).thenReturn(
                new AdaptiveEngine.Decision(false, false, "next", InterviewSession.Difficulty.INTERMEDIATE));
        when(adaptiveEngine.nextDifficulty(any(), any())).thenReturn(InterviewSession.Difficulty.INTERMEDIATE);

        when(questionRepository.findAllByInterviewIdOrderByOrderAsc("i-1"))
                .thenReturn(List.of(question));

        AIService.GeneratedQuestion gen = new AIService.GeneratedQuestion(
                "Next question?", "BASIC", List.of("x"), "INTERMEDIATE", "RESUME");
        when(aiService.generateInterviewQuestion(anyString(), any(AIService.QuestionContext.class)))
                .thenReturn(gen);
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> {
            Question q = inv.getArgument(0); q.setId("q-2"); return q;
        });

        AnswerRequest request = AnswerRequest.builder()
                .questionId("q-1").text("DI is a pattern").build();

        AnswerResponse response = service.submitAnswer("u-1", "i-1", request);

        assertThat(response.getEvaluation().getOverall()).isEqualTo(80);
        assertThat(response.getNextQuestion()).isNotNull();
        assertThat(response.getNextQuestion().getQuestion().getId()).isEqualTo("q-2");
    }

    private InterviewSession inProgressSession() {
        return InterviewSession.builder()
                .id("i-1").userId("u-1").resumeId("r-1")
                .type(InterviewSession.Type.TECHNICAL)
                .mode(InterviewSession.Mode.TEXT)
                .difficulty(InterviewSession.Difficulty.INTERMEDIATE)
                .personality(InterviewSession.Personality.PROFESSIONAL)
                .status(InterviewSession.Status.IN_PROGRESS)
                .startedAt(Instant.now())
                .questionIds(new ArrayList<>())
                .state(InterviewState.builder()
                        .currentQuestionIndex(1)
                        .totalQuestionsAsked(0)
                        .cumulativeScore(0)
                        .weakAreas(new ArrayList<>())
                        .strongAreas(new ArrayList<>())
                        .build())
                .build();
    }
}
