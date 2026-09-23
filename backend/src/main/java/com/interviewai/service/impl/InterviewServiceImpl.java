package com.interviewai.service.impl;

import com.interviewai.ai.AIService;
import com.interviewai.analytics.CommunicationAnalyzer;
import com.interviewai.dto.interview.AnswerRequest;
import com.interviewai.dto.interview.AnswerResponse;
import com.interviewai.dto.interview.CreateInterviewRequest;
import com.interviewai.dto.interview.InterviewResponse;
import com.interviewai.dto.interview.InterviewStateResponse;
import com.interviewai.dto.interview.InterviewSummaryResponse;
import com.interviewai.dto.interview.NextQuestionResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.InterviewMapper;
import com.interviewai.model.AiUsage;
import com.interviewai.model.Answer;
import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.JobDescription;
import com.interviewai.model.Question;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.AnswerMetrics;
import com.interviewai.model.embedded.InterviewState;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.AnswerRepository;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.JobDescriptionRepository;
import com.interviewai.repository.QuestionRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.AdaptiveEngine;
import com.interviewai.service.InterviewService;
import com.interviewai.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InterviewServiceImpl implements InterviewService {

    private static final Logger log = LoggerFactory.getLogger(InterviewServiceImpl.class);

    private final InterviewSessionRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final EvaluationRepository evaluationRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobRepository;
    private final AIService aiService;
    private final AdaptiveEngine adaptiveEngine;
    private final InterviewMapper mapper;
    private final AiUsageRepository aiUsageRepository;
    private final CommunicationAnalyzer communicationAnalyzer;
    private final ReportService reportService;

    public InterviewServiceImpl(InterviewSessionRepository interviewRepository,
                                QuestionRepository questionRepository,
                                AnswerRepository answerRepository,
                                EvaluationRepository evaluationRepository,
                                ResumeRepository resumeRepository,
                                JobDescriptionRepository jobRepository,
                                AIService aiService,
                                AdaptiveEngine adaptiveEngine,
                                InterviewMapper mapper,
                                AiUsageRepository aiUsageRepository,
                                CommunicationAnalyzer communicationAnalyzer,
                                @Lazy ReportService reportService) {
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.evaluationRepository = evaluationRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.aiService = aiService;
        this.adaptiveEngine = adaptiveEngine;
        this.mapper = mapper;
        this.aiUsageRepository = aiUsageRepository;
        this.communicationAnalyzer = communicationAnalyzer;
        this.reportService = reportService;
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------
    @Override
    public InterviewResponse create(String userId, CreateInterviewRequest request) {
        String resumeId = resolveResumeId(userId, request.getResumeId());

        if (request.getJobDescriptionId() != null && !request.getJobDescriptionId().isBlank()) {
            jobRepository.findByIdAndUserId(request.getJobDescriptionId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job description not found"));
        }

        InterviewSession session = InterviewSession.builder()
                .userId(userId)
                .resumeId(resumeId)
                .jobDescriptionId(request.getJobDescriptionId())
                .type(request.getType())
                .mode(request.getMode())
                .difficulty(request.getDifficulty())
                .personality(request.getPersonality())
                .status(InterviewSession.Status.SCHEDULED)
                .state(InterviewState.builder()
                        .currentQuestionIndex(0)
                        .totalQuestionsAsked(0)
                        .cumulativeScore(0)
                        .weakAreas(new ArrayList<>())
                        .strongAreas(new ArrayList<>())
                        .lastUpdatedAt(Instant.now())
                        .build())
                .questionIds(new ArrayList<>())
                .focusTopic(request.getFocusTopic())
                .build();

        session = interviewRepository.save(session);
        log.info("Interview created id={} user={} type={}", session.getId(), userId, session.getType());
        return mapper.toResponse(session);
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------
    @Override
    public List<InterviewSummaryResponse> list(String userId) {
        return interviewRepository.findAllByUserIdOrderByStartedAtDesc(userId).stream()
                .map(mapper::toSummary)
                .toList();
    }

    @Override
    public InterviewResponse get(String userId, String interviewId) {
        return mapper.toResponse(getOwned(userId, interviewId));
    }

    @Override
    public InterviewSession getOwned(String userId, String interviewId) {
        return interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
    }

    @Override
    public InterviewStateResponse getState(String userId, String interviewId) {
        InterviewSession session = getOwned(userId, interviewId);
        return toStateResponse(session);
    }

    // ------------------------------------------------------------------
    // START
    // ------------------------------------------------------------------
    @Override
    public NextQuestionResponse start(String userId, String interviewId) {
        InterviewSession session = getOwned(userId, interviewId);

        if (session.getStatus() == InterviewSession.Status.COMPLETED
                || session.getStatus() == InterviewSession.Status.ABANDONED) {
            throw new BadRequestException("Interview cannot be started in its current state");
        }

        if (session.getQuestionIds() != null && !session.getQuestionIds().isEmpty()) {
            // Already started — return the latest question
            String lastId = session.getQuestionIds().get(session.getQuestionIds().size() - 1);
            Question last = questionRepository.findById(lastId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
            return NextQuestionResponse.builder()
                    .interviewId(session.getId())
                    .status(session.getStatus())
                    .question(mapper.toQuestionResponse(last))
                    .questionNumber(session.getQuestionIds().size())
                    .weakAreas(session.getState().getWeakAreas())
                    .strongAreas(session.getState().getStrongAreas())
                    .cumulativeScore(session.getState().getCumulativeScore())
                    .completed(false)
                    .message("Interview already started")
                    .build();
        }

        session.setStatus(InterviewSession.Status.IN_PROGRESS);
        session.setStartedAt(Instant.now());
        session = interviewRepository.save(session);

        Question first = generateNextQuestion(session, false, null);
        session.getQuestionIds().add(first.getId());
        session.getState().setCurrentQuestionIndex(1);
        session.getState().setTotalQuestionsAsked(0);
        interviewRepository.save(session);

        return NextQuestionResponse.builder()
                .interviewId(session.getId())
                .status(session.getStatus())
                .question(mapper.toQuestionResponse(first))
                .questionNumber(1)
                .weakAreas(session.getState().getWeakAreas())
                .strongAreas(session.getState().getStrongAreas())
                .cumulativeScore(0.0)
                .completed(false)
                .message("Interview started")
                .build();
    }

    // ------------------------------------------------------------------
    // NEXT
    // ------------------------------------------------------------------
    @Override
    public NextQuestionResponse nextQuestion(String userId, String interviewId) {
        InterviewSession session = getOwned(userId, interviewId);

        if (session.getStatus() != InterviewSession.Status.IN_PROGRESS) {
            throw new BadRequestException("Interview is not in progress");
        }

        List<Question> asked = questionRepository.findAllByInterviewIdOrderByOrderAsc(session.getId());
        Evaluation lastEval = asked.isEmpty() ? null
                : evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc(session.getId())
                        .stream().reduce((a, b) -> b).orElse(null);

        AdaptiveEngine.Decision decision = adaptiveEngine.decideNext(session, lastEval, asked);

        if (decision.endInterview()) {
            InterviewStateResponse completed = completeInternal(session, decision.reason());
            return NextQuestionResponse.builder()
                    .interviewId(session.getId())
                    .status(completed.getStatus())
                    .question(null)
                    .questionNumber(session.getQuestionIds().size())
                    .weakAreas(completed.getWeakAreas())
                    .strongAreas(completed.getStrongAreas())
                    .cumulativeScore(completed.getCumulativeScore())
                    .completed(true)
                    .message(decision.reason())
                    .build();
        }

        Question next = generateNextQuestion(session, decision.followUp(), lastEval);
        session.getQuestionIds().add(next.getId());
        session.getState().setCurrentQuestionIndex(session.getState().getCurrentQuestionIndex() + 1);
        interviewRepository.save(session);

        return NextQuestionResponse.builder()
                .interviewId(session.getId())
                .status(session.getStatus())
                .question(mapper.toQuestionResponse(next))
                .questionNumber(session.getQuestionIds().size())
                .weakAreas(session.getState().getWeakAreas())
                .strongAreas(session.getState().getStrongAreas())
                .cumulativeScore(session.getState().getCumulativeScore())
                .completed(false)
                .message(decision.reason())
                .build();
    }

    // ------------------------------------------------------------------
    // ANSWER
    // ------------------------------------------------------------------
    @Override
    public AnswerResponse submitAnswer(String userId, String interviewId, AnswerRequest request) {
        InterviewSession session = getOwned(userId, interviewId);

        if (session.getStatus() != InterviewSession.Status.IN_PROGRESS) {
            throw new BadRequestException("Interview is not in progress");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (!question.getInterviewId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to this interview");
        }

        // Compute text metrics for the answer
        AnswerMetrics metrics = communicationAnalyzer.analyze(request.getText(), null);

        // Persist answer
        Answer answer = Answer.builder()
                .interviewId(session.getId())
                .questionId(question.getId())
                .userId(userId)
                .text(request.getText())
                .audioUrl(request.getAudioUrl())
                .videoUrl(request.getVideoUrl())
                .metrics(metrics)
                .answeredAt(Instant.now())
                .build();
        answer = answerRepository.save(answer);

        // Evaluate
        long start = System.currentTimeMillis();
        Evaluation evaluation = aiService.evaluateAnswer(userId, new AIService.EvaluationContext(
                session.getType().name(),
                session.getDifficulty().name(),
                question.getText(),
                question.getExpectedTopics(),
                request.getText()
        ));
        recordUsage(userId, AiUsage.Operation.EVALUATE_ANSWER, System.currentTimeMillis() - start);

        evaluation.setInterviewId(session.getId());
        evaluation.setQuestionId(question.getId());
        evaluation.setAnswerId(answer.getId());
        evaluation.setUserId(userId);
        evaluation.setCreatedAt(Instant.now());
        evaluation = evaluationRepository.save(evaluation);

        // Update adaptive state
        adaptiveEngine.updateState(session, evaluation);
        session.setDifficulty(adaptiveEngine.nextDifficulty(session.getDifficulty(), evaluation));
        interviewRepository.save(session);

        // Decide next step
        List<Question> asked = questionRepository.findAllByInterviewIdOrderByOrderAsc(session.getId());
        AdaptiveEngine.Decision decision = adaptiveEngine.decideNext(session, evaluation, asked);

        if (decision.endInterview()) {
            InterviewStateResponse completed = completeInternal(session, decision.reason());
            return AnswerResponse.builder()
                    .interviewId(session.getId())
                    .answerId(answer.getId())
                    .questionId(question.getId())
                    .evaluation(evaluation)
                    .status(InterviewSession.Status.COMPLETED)
                    .weakAreas(completed.getWeakAreas())
                    .strongAreas(completed.getStrongAreas())
                    .cumulativeScore(completed.getCumulativeScore())
                    .completed(true)
                    .nextQuestion(null)
                    .build();
        }

        Question next = generateNextQuestion(session, decision.followUp(), evaluation);
        session.getQuestionIds().add(next.getId());
        session.getState().setCurrentQuestionIndex(session.getState().getCurrentQuestionIndex() + 1);
        interviewRepository.save(session);

        NextQuestionResponse nextResponse = NextQuestionResponse.builder()
                .interviewId(session.getId())
                .status(session.getStatus())
                .question(mapper.toQuestionResponse(next))
                .questionNumber(session.getQuestionIds().size())
                .weakAreas(session.getState().getWeakAreas())
                .strongAreas(session.getState().getStrongAreas())
                .cumulativeScore(session.getState().getCumulativeScore())
                .completed(false)
                .message(decision.reason())
                .build();

        return AnswerResponse.builder()
                .interviewId(session.getId())
                .answerId(answer.getId())
                .questionId(question.getId())
                .evaluation(evaluation)
                .nextQuestion(nextResponse)
                .status(session.getStatus())
                .weakAreas(session.getState().getWeakAreas())
                .strongAreas(session.getState().getStrongAreas())
                .cumulativeScore(session.getState().getCumulativeScore())
                .completed(false)
                .build();
    }

    // ------------------------------------------------------------------
    // COMPLETE
    // ------------------------------------------------------------------
    @Override
    public InterviewStateResponse complete(String userId, String interviewId) {
        InterviewSession session = getOwned(userId, interviewId);
        return completeInternal(session, "Manually completed by user");
    }

    // ------------------------------------------------------------------
    // INTERNALS
    // ------------------------------------------------------------------
    private Question generateNextQuestion(InterviewSession session, boolean followUp, Evaluation lastEvaluation) {
        ParsedResume resume = loadResume(session);
        ParsedJobDescription jd = loadJobDescription(session);
        List<String> asked = questionRepository.findAllByInterviewIdOrderByOrderAsc(session.getId()).stream()
                .map(Question::getText)
                .toList();

        long start = System.currentTimeMillis();
        AIService.GeneratedQuestion generated;
        if (followUp && lastEvaluation != null && !asked.isEmpty()) {
            String lastQuestion = asked.get(asked.size() - 1);
            Answer lastAnswer = answerRepository.findAllByInterviewIdOrderByAnsweredAtAsc(session.getId()).stream()
                    .reduce((a, b) -> b).orElse(null);
            String lastText = lastAnswer != null ? lastAnswer.getText() : "";
            generated = aiService.generateFollowUpQuestion(session.getUserId(),
                    new AIService.FollowUpContext(
                            lastQuestion,
                            lastText,
                            "Overall=" + lastEvaluation.getOverall() + ", feedback=" + lastEvaluation.getFeedback(),
                            session.getState().getWeakAreas()
                    ));
            recordUsage(session.getUserId(), AiUsage.Operation.GENERATE_FOLLOW_UP, System.currentTimeMillis() - start);
        } else {
            generated = aiService.generateInterviewQuestion(session.getUserId(),
                    new AIService.QuestionContext(
                            session.getType().name(),
                            session.getDifficulty().name(),
                            session.getPersonality().name(),
                            session.getMode().name(),
                            resume,
                            jd,
                            asked,
                            session.getState().getWeakAreas(),
                            session.getState().getStrongAreas(),
                            session.getState().getRollingSummary(),
                            session.getFocusTopic()
                    ));
            recordUsage(session.getUserId(), AiUsage.Operation.GENERATE_QUESTION, System.currentTimeMillis() - start);
        }

        Question question = Question.builder()
                .interviewId(session.getId())
                .order(asked.size() + 1)
                .type(parseQuestionType(generated.type()))
                .text(generated.question())
                .expectedTopics(generated.expectedTopics())
                .difficulty(generated.difficulty())
                .generatedFrom(parseGeneratedFrom(generated.generatedFrom()))
                .createdAt(Instant.now())
                .build();
        return questionRepository.save(question);
    }

    private InterviewStateResponse completeInternal(InterviewSession session, String reason) {
        session.setStatus(InterviewSession.Status.COMPLETED);
        session.setCompletedAt(Instant.now());
        if (session.getStartedAt() != null) {
            session.setDurationSec(Duration.between(session.getStartedAt(), session.getCompletedAt()).getSeconds());
        }
        session.getState().setCompleted(true);
        interviewRepository.save(session);
        log.info("Interview completed id={} reason={}", session.getId(), reason);

        // Auto-generate the final report (best-effort).
        try {
            reportService.generate(session.getUserId(), session.getId(), false);
        } catch (Exception e) {
            log.warn("Auto-report generation failed for interview {}: {}", session.getId(), e.getMessage());
        }

        return toStateResponse(session);
    }

    private InterviewStateResponse toStateResponse(InterviewSession session) {
        return InterviewStateResponse.builder()
                .interviewId(session.getId())
                .status(session.getStatus())
                .currentQuestionIndex(session.getState().getCurrentQuestionIndex())
                .totalQuestionsAsked(session.getState().getTotalQuestionsAsked())
                .currentTopic(session.getState().getCurrentTopic())
                .weakAreas(session.getState().getWeakAreas())
                .strongAreas(session.getState().getStrongAreas())
                .cumulativeScore(session.getState().getCumulativeScore())
                .completed(session.getState().isCompleted())
                .build();
    }

    private String resolveResumeId(String userId, String requested) {
        if (requested != null && !requested.isBlank()) {
            resumeRepository.findByIdAndUserId(requested, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
            return requested;
        }
        return resumeRepository.findFirstByUserIdAndPrimaryResumeTrue(userId)
                .or(() -> resumeRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().findFirst())
                .orElseThrow(() -> new BadRequestException("No resume uploaded yet"))
                .getId();
    }

    private ParsedResume loadResume(InterviewSession session) {
        return resumeRepository.findByIdAndUserId(session.getResumeId(), session.getUserId())
                .map(Resume::getParsed)
                .orElse(null);
    }

    private ParsedJobDescription loadJobDescription(InterviewSession session) {
        if (session.getJobDescriptionId() == null) return null;
        Optional<JobDescription> jd = jobRepository.findByIdAndUserId(session.getJobDescriptionId(), session.getUserId());
        return jd.map(JobDescription::getParsed).orElse(null);
    }

    private Question.Type parseQuestionType(String type) {
        if (type == null) return Question.Type.BASIC;
        try {
            return Question.Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Question.Type.BASIC;
        }
    }

    private Question.GeneratedFrom parseGeneratedFrom(String value) {
        if (value == null) return Question.GeneratedFrom.RESUME;
        try {
            return Question.GeneratedFrom.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Question.GeneratedFrom.RESUME;
        }
    }

    private void recordUsage(String userId, AiUsage.Operation op, long latencyMs) {
        Map<String, Object> meta = aiService.lastUsageMetadata();
        AiUsage usage = AiUsage.builder()
                .userId(userId)
                .operation(op)
                .model(meta.getOrDefault("model", "unknown").toString())
                .promptTokens(intOf(meta.get("promptTokens")))
                .completionTokens(intOf(meta.get("completionTokens")))
                .totalTokens(intOf(meta.get("totalTokens")))
                .latencyMs(latencyMs)
                .success(true)
                .createdAt(Instant.now())
                .build();
        aiUsageRepository.save(usage);
    }

    private int intOf(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
