package com.interviewai.ai;

import com.interviewai.ai.client.dto.ChatCompletionResponse;
import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.model.Evaluation;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.model.embedded.ReportSections;

import java.util.List;
import java.util.Map;

public interface AIService {

    ParsedResume analyzeResume(String userId, String rawText);

    ParsedJobDescription analyzeJobDescription(String userId, String rawText);

    MatchResult matchResumeWithJob(String userId, ParsedResume resume, ParsedJobDescription jd);

    /**
     * Generate the next interview question.
     */
    GeneratedQuestion generateInterviewQuestion(String userId, QuestionContext context);

    GeneratedQuestion generateFollowUpQuestion(String userId, FollowUpContext context);

    Evaluation evaluateAnswer(String userId, EvaluationContext context);

    ReportResult generateFinalReport(String userId, ReportContext context);

    List<LearningItem> generateLearningPlan(String userId, List<String> weakAreas, String level);

    // ---------- helper records ----------

    record GeneratedQuestion(
            String question,
            String type,
            List<String> expectedTopics,
            String difficulty,
            String generatedFrom
    ) {}

    record QuestionContext(
            String interviewType,
            String difficulty,
            String personality,
            String mode,
            ParsedResume resume,
            ParsedJobDescription jd,
            List<String> askedQuestions,
            List<String> weakAreas,
            List<String> strongAreas,
            String rollingSummary,
            String focusTopic
    ) {}

    record FollowUpContext(
            String lastQuestion,
            String lastAnswer,
            String evaluationSummary,
            List<String> weakAreas
    ) {}

    record EvaluationContext(
            String interviewType,
            String difficulty,
            String question,
            List<String> expectedTopics,
            String answer
    ) {}

    record ReportContext(
            String interviewType,
            String difficulty,
            ParsedResume resume,
            List<Evaluation> evaluations,
            List<String> weakAreas,
            List<String> strongAreas
    ) {}

    record ReportResult(
            double overallScore,
            ReportSections sections,
            List<String> strongAreas,
            List<String> weakAreas,
            List<String> recommendedTopics,
            String summary
    ) {}

    /**
     * Low-level pass-through used for tests/monitoring.
     */
    ChatCompletionResponse rawChat(List<com.interviewai.ai.client.dto.ChatMessage> messages, double temperature, int maxTokens);

    /**
     * Free-form usage metadata for AiUsage collection.
     */
    Map<String, Object> lastUsageMetadata();
}
