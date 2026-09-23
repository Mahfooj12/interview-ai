package com.interviewai.service.impl;

import com.interviewai.ai.AIService;
import com.interviewai.dto.report.GenerateReportResponse;
import com.interviewai.dto.report.ReportResponse;
import com.interviewai.dto.report.ReportSummaryResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.mapper.ReportMapper;
import com.interviewai.model.AiUsage;
import com.interviewai.model.Answer;
import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;
import com.interviewai.model.Report;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.AnswerMetrics;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.AnswerRepository;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.QuestionRepository;
import com.interviewai.repository.ReportRepository;
import com.interviewai.repository.ResumeRepository;
import com.interviewai.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;
    private final InterviewSessionRepository interviewRepository;
    private final EvaluationRepository evaluationRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ResumeRepository resumeRepository;
    private final AIService aiService;
    private final ReportMapper reportMapper;
    private final AiUsageRepository aiUsageRepository;

    public ReportServiceImpl(ReportRepository reportRepository,
                             InterviewSessionRepository interviewRepository,
                             EvaluationRepository evaluationRepository,
                             AnswerRepository answerRepository,
                             QuestionRepository questionRepository,
                             ResumeRepository resumeRepository,
                             AIService aiService,
                             ReportMapper reportMapper,
                             AiUsageRepository aiUsageRepository) {
        this.reportRepository = reportRepository;
        this.interviewRepository = interviewRepository;
        this.evaluationRepository = evaluationRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.resumeRepository = resumeRepository;
        this.aiService = aiService;
        this.reportMapper = reportMapper;
        this.aiUsageRepository = aiUsageRepository;
    }

    // ------------------------------------------------------------------
    // GENERATE
    // ------------------------------------------------------------------
    @Override
    public GenerateReportResponse generate(String userId, String interviewId, boolean force) {
        InterviewSession session = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        Report existing = reportRepository.findByInterviewId(interviewId).orElse(null);
        if (existing != null && !force) {
            return GenerateReportResponse.builder()
                    .reportId(existing.getId())
                    .interviewId(interviewId)
                    .generated(false)
                    .message("Report already exists")
                    .build();
        }

        List<Evaluation> evaluations = evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc(interviewId);
        if (evaluations.isEmpty()) {
            throw new BadRequestException("Cannot generate a report without any evaluated answers");
        }

        // Ensure the interview is marked completed
        if (session.getStatus() != InterviewSession.Status.COMPLETED) {
            session.setStatus(InterviewSession.Status.COMPLETED);
            session.setCompletedAt(Instant.now());
            if (session.getStartedAt() != null && session.getCompletedAt() != null) {
                session.setDurationSec(java.time.Duration.between(session.getStartedAt(), session.getCompletedAt()).getSeconds());
            }
            session.getState().setCompleted(true);
            interviewRepository.save(session);
        }

        // Build report via AI
        Resume resume = resumeRepository.findByIdAndUserId(session.getResumeId(), userId).orElse(null);
        AIService.ReportResult aiResult = aiService.generateFinalReport(userId,
                new AIService.ReportContext(
                        session.getType().name(),
                        session.getDifficulty().name(),
                        resume != null ? resume.getParsed() : null,
                        evaluations,
                        session.getState().getWeakAreas(),
                        session.getState().getStrongAreas()
                ));

        // Learning roadmap
        List<String> weakTopics = aiResult.weakAreas().isEmpty()
                ? session.getState().getWeakAreas()
                : aiResult.weakAreas();

        long startLp = System.currentTimeMillis();
        List<LearningItem> roadmap = aiService.generateLearningPlan(userId, weakTopics, session.getDifficulty().name());
        recordUsage(userId, AiUsage.Operation.GENERATE_LEARNING_PLAN, System.currentTimeMillis() - startLp);

        // Question-level feedback + aggregated communication stats
        List<Report.QuestionFeedback> qf = buildQuestionFeedback(interviewId, evaluations);
        AnswerMetrics communicationStats = aggregateCommunication(interviewId);

        Report report = existing != null ? existing : Report.builder()
                .interviewId(interviewId)
                .userId(userId)
                .build();

        report.setSections(aiResult.sections());
        report.setStrongAreas(aiResult.strongAreas());
        report.setWeakAreas(aiResult.weakAreas());
        report.setRecommendedTopics(aiResult.recommendedTopics());
        report.setLearningRoadmap(roadmap);
        report.setQuestionFeedback(qf);
        report.setCommunicationStats(communicationStats);
        report.setSummary(aiResult.summary());
        report.setGeneratedAt(Instant.now());

        report = reportRepository.save(report);
        log.info("Report generated id={} interviewId={}", report.getId(), interviewId);

        return GenerateReportResponse.builder()
                .reportId(report.getId())
                .interviewId(interviewId)
                .generated(true)
                .message("Report generated successfully")
                .build();
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------
    @Override
    public ReportResponse get(String userId, String reportId) {
        Report report = reportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return reportMapper.toResponse(report);
    }

    @Override
    public ReportResponse getByInterview(String userId, String interviewId) {
        Report report = reportRepository.findByInterviewId(interviewId)
                .filter(r -> r.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return reportMapper.toResponse(report);
    }

    @Override
    public List<ReportSummaryResponse> list(String userId) {
        return reportRepository.findAllByUserIdOrderByGeneratedAtDesc(userId).stream()
                .map(reportMapper::toSummary)
                .toList();
    }

    // ------------------------------------------------------------------
    // INTERNAL
    // ------------------------------------------------------------------
    private List<Report.QuestionFeedback> buildQuestionFeedback(String interviewId, List<Evaluation> evaluations) {
        Map<String, Question> questions = new HashMap<>();
        for (Question q : questionRepository.findAllByInterviewIdOrderByOrderAsc(interviewId)) {
            questions.put(q.getId(), q);
        }
        Map<String, Answer> answers = new HashMap<>();
        for (Answer a : answerRepository.findAllByInterviewIdOrderByAnsweredAtAsc(interviewId)) {
            answers.put(a.getQuestionId(), a);
        }

        List<Report.QuestionFeedback> result = new ArrayList<>();
        for (Evaluation e : evaluations) {
            Question q = questions.get(e.getQuestionId());
            Answer a = answers.get(e.getQuestionId());
            result.add(Report.QuestionFeedback.builder()
                    .questionId(e.getQuestionId())
                    .question(q != null ? q.getText() : null)
                    .answer(a != null ? a.getText() : null)
                    .score(e.getOverall())
                    .feedback(e.getFeedback())
                    .idealAnswer(e.getIdealAnswer())
                    .build());
        }
        return result;
    }

    private AnswerMetrics aggregateCommunication(String interviewId) {
        List<Answer> answers = answerRepository.findAllByInterviewIdOrderByAnsweredAtAsc(interviewId);
        if (answers.isEmpty()) return null;

        double wpmSum = 0;
        int filler = 0;
        int hesitation = 0;
        double clarity = 0;
        double confidence = 0;
        double grammar = 0;
        double tone = 0;
        int n = 0;

        for (Answer a : answers) {
            AnswerMetrics m = a.getMetrics();
            if (m == null) continue;
            wpmSum += m.getWpm();
            filler += m.getFillerWordCount();
            hesitation += m.getHesitationCount();
            clarity += m.getClarityScore();
            confidence += m.getConfidenceScore();
            grammar += m.getGrammarScore();
            tone += m.getToneScore();
            n++;
        }
        if (n == 0) return null;

        return AnswerMetrics.builder()
                .wpm(round(wpmSum / n))
                .fillerWordCount(filler)
                .fillerWords(List.of())
                .hesitationCount(hesitation)
                .clarityScore(round(clarity / n))
                .confidenceScore(round(confidence / n))
                .grammarScore(round(grammar / n))
                .toneScore(round(tone / n))
                .build();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
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
