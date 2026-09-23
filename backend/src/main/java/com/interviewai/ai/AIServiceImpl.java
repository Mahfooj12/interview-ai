package com.interviewai.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.client.AiClient;
import com.interviewai.ai.client.dto.ChatCompletionResponse;
import com.interviewai.ai.client.dto.ChatMessage;
import com.interviewai.ai.prompt.PromptSanitizer;
import com.interviewai.ai.prompt.PromptTemplates;
import com.interviewai.exception.BadRequestException;
import com.interviewai.model.Evaluation;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.model.embedded.ReportSections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);

    private final AiClient client;              // <-- was DeepSeekClient
    private final PromptSanitizer sanitizer;
    private final ObjectMapper objectMapper;

    private final ThreadLocal<Map<String, Object>> lastUsage = new ThreadLocal<>();

    public AIServiceImpl(AiClient client,       // <-- was DeepSeekClient
                         PromptSanitizer sanitizer,
                         ObjectMapper objectMapper) {
        this.client = client;
        this.sanitizer = sanitizer;
        this.objectMapper = objectMapper;
    }

    // ------------------------------------------------------------------
    // RESUME
    // ------------------------------------------------------------------
    @Override
    public ParsedResume analyzeResume(String userId, String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new BadRequestException("Resume text is empty");
        }
        if (sanitizer.looksLikeInjection(rawText)) {
            log.warn("Possible prompt injection detected in resume for user {}", userId);
        }
        String wrapped = sanitizer.wrap("RESUME", rawText);
        String user = String.format(PromptTemplates.RESUME_PARSER_USER_TEMPLATE, wrapped);

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.RESUME_PARSER_SYSTEM),
                ChatMessage.user(user)
        ), 0.1, 4000);

        try {
            return objectMapper.readValue(json, ParsedResume.class);
        } catch (Exception e) {
            log.error("Failed to parse resume JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid resume JSON");
        }
    }

    // ------------------------------------------------------------------
    // JOB DESCRIPTION
    // ------------------------------------------------------------------
    @Override
    public ParsedJobDescription analyzeJobDescription(String userId, String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new BadRequestException("Job description text is empty");
        }
        if (sanitizer.looksLikeInjection(rawText)) {
            log.warn("Possible prompt injection detected in JD for user {}", userId);
        }
        String wrapped = sanitizer.wrap("JOB_DESCRIPTION", rawText);
        String user = String.format(PromptTemplates.JD_PARSER_USER_TEMPLATE, wrapped);

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.JD_PARSER_SYSTEM),
                ChatMessage.user(user)
        ), 0.1, 2000);

        try {
            return objectMapper.readValue(json, ParsedJobDescription.class);
        } catch (Exception e) {
            log.error("Failed to parse JD JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid job description JSON");
        }
    }

    // ------------------------------------------------------------------
    // MATCHING
    // ------------------------------------------------------------------
    @Override
    public MatchResult matchResumeWithJob(String userId, ParsedResume resume, ParsedJobDescription jd) {
        String resumeJson = safeWrite(resume);
        String jdJson = safeWrite(jd);
        String user = String.format(PromptTemplates.MATCH_USER_TEMPLATE,
                sanitizer.wrap("RESUME_JSON", resumeJson),
                sanitizer.wrap("JD_JSON", jdJson));

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.MATCH_SYSTEM),
                ChatMessage.user(user)
        ), 0.1, 1500);

        try {
            return objectMapper.readValue(json, MatchResult.class);
        } catch (Exception e) {
            log.error("Failed to parse match JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid match JSON");
        }
    }

    // ------------------------------------------------------------------
    // QUESTION GENERATION
    // ------------------------------------------------------------------
    @Override
    public GeneratedQuestion generateInterviewQuestion(String userId, QuestionContext ctx) {
        String askedJson = safeWrite(ctx.askedQuestions() == null ? List.of() : ctx.askedQuestions());
        String user = String.format(PromptTemplates.QUESTION_USER_TEMPLATE,
                ctx.interviewType(),
                ctx.difficulty(),
                ctx.personality(),
                ctx.mode(),
                sanitizer.wrap("RESUME_JSON", safeWrite(ctx.resume())),
                sanitizer.wrap("JD_JSON", safeWrite(ctx.jd())),
                sanitizer.wrap("ASKED_QUESTIONS", askedJson),
                ctx.weakAreas(),
                ctx.strongAreas(),
                ctx.rollingSummary() == null ? "(none yet)" : ctx.rollingSummary(),
                ctx.focusTopic() == null ? "(none)" : ctx.focusTopic());

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.QUESTION_SYSTEM),
                ChatMessage.user(user)
        ), 0.7, 800);

        try {
            JsonNode root = objectMapper.readTree(json);
            return new GeneratedQuestion(
                    root.path("question").asText(),
                    root.path("type").asText("BASIC"),
                    toStringList(root.path("expectedTopics")),
                    root.path("difficulty").asText(ctx.difficulty()),
                    root.path("generatedFrom").asText("RESUME")
            );
        } catch (Exception e) {
            log.error("Failed to parse question JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid question JSON");
        }
    }

    @Override
    public GeneratedQuestion generateFollowUpQuestion(String userId, FollowUpContext ctx) {
        String user = String.format(PromptTemplates.FOLLOW_UP_USER_TEMPLATE,
                sanitizer.wrap("LAST_QUESTION", ctx.lastQuestion()),
                sanitizer.wrap("LAST_ANSWER", ctx.lastAnswer()),
                sanitizer.wrap("EVAL_SUMMARY", ctx.evaluationSummary()),
                ctx.weakAreas());

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.FOLLOW_UP_SYSTEM),
                ChatMessage.user(user)
        ), 0.6, 600);

        try {
            JsonNode root = objectMapper.readTree(json);
            return new GeneratedQuestion(
                    root.path("question").asText(),
                    "FOLLOW_UP",
                    toStringList(root.path("expectedTopics")),
                    root.path("difficulty").asText("INTERMEDIATE"),
                    "PREVIOUS_ANSWER"
            );
        } catch (Exception e) {
            log.error("Failed to parse follow-up JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid follow-up JSON");
        }
    }

    // ------------------------------------------------------------------
    // EVALUATION
    // ------------------------------------------------------------------
    @Override
    public Evaluation evaluateAnswer(String userId, EvaluationContext ctx) {
        String user = String.format(PromptTemplates.EVALUATION_USER_TEMPLATE,
                ctx.interviewType(),
                ctx.difficulty(),
                sanitizer.wrap("QUESTION", ctx.question()),
                ctx.expectedTopics(),
                sanitizer.wrap("ANSWER", ctx.answer()));

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.EVALUATION_SYSTEM),
                ChatMessage.user(user)
        ), 0.2, 1200);

        try {
            JsonNode root = objectMapper.readTree(json);
            return Evaluation.builder()
                    .technicalAccuracy(root.path("technicalAccuracy").asDouble())
                    .relevance(root.path("relevance").asDouble())
                    .depth(root.path("depth").asDouble())
                    .clarity(root.path("clarity").asDouble())
                    .communication(root.path("communication").asDouble())
                    .confidence(root.path("confidence").asDouble())
                    .overall(root.path("overall").asDouble())
                    .strengths(toStringList(root.path("strengths")))
                    .weaknesses(toStringList(root.path("weaknesses")))
                    .weakAreasDetected(toStringList(root.path("weakAreasDetected")))
                    .feedback(root.path("feedback").asText(""))
                    .idealAnswer(root.path("idealAnswer").asText(""))
                    .followUpNeeded(root.path("followUpNeeded").asBoolean(false))
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse evaluation JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid evaluation JSON");
        }
    }

    // ------------------------------------------------------------------
    // REPORT
    // ------------------------------------------------------------------
    @Override
    public ReportResult generateFinalReport(String userId, ReportContext ctx) {
        String evaluationsJson = safeWrite(ctx.evaluations());
        String user = String.format(PromptTemplates.REPORT_USER_TEMPLATE,
                ctx.interviewType(),
                ctx.difficulty(),
                sanitizer.wrap("CANDIDATE_CONTEXT", safeWrite(ctx.resume())),
                sanitizer.wrap("EVALUATIONS", evaluationsJson));

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.REPORT_SYSTEM),
                ChatMessage.user(user)
        ), 0.3, 2000);

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode s = root.path("sections");
            ReportSections sections = ReportSections.builder()
                    .overall(root.path("overallScore").asDouble())
                    .technical(s.path("technical").asDouble())
                    .communication(s.path("communication").asDouble())
                    .problemSolving(s.path("problemSolving").asDouble())
                    .confidence(s.path("confidence").asDouble())
                    .resumeKnowledge(s.path("resumeKnowledge").asDouble())
                    .projectKnowledge(s.path("projectKnowledge").asDouble())
                    .build();

            return new ReportResult(
                    root.path("overallScore").asDouble(),
                    sections,
                    toStringList(root.path("strongAreas")),
                    toStringList(root.path("weakAreas")),
                    toStringList(root.path("recommendedTopics")),
                    root.path("summary").asText("")
            );
        } catch (Exception e) {
            log.error("Failed to parse report JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid report JSON");
        }
    }

    // ------------------------------------------------------------------
    // LEARNING PLAN
    // ------------------------------------------------------------------
    @Override
    public List<LearningItem> generateLearningPlan(String userId, List<String> weakAreas, String level) {
        if (weakAreas == null || weakAreas.isEmpty()) return List.of();
        String user = String.format(PromptTemplates.LEARNING_PLAN_USER_TEMPLATE,
                weakAreas, level == null ? "INTERMEDIATE" : level);

        String json = callJson(List.of(
                ChatMessage.system(PromptTemplates.LEARNING_PLAN_SYSTEM),
                ChatMessage.user(user)
        ), 0.3, 1500);

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root.path("items");
            List<LearningItem> result = new ArrayList<>();
            if (items.isArray()) {
                for (JsonNode item : items) {
                    List<LearningItem.Resource> resources = new ArrayList<>();
                    JsonNode res = item.path("resources");
                    if (res.isArray()) {
                        for (JsonNode r : res) {
                            resources.add(LearningItem.Resource.builder()
                                    .type(r.path("type").asText())
                                    .title(r.path("title").asText())
                                    .url(r.path("url").asText())
                                    .build());
                        }
                    }
                    result.add(LearningItem.builder()
                            .topic(item.path("topic").asText())
                            .priority(item.path("priority").asText("MEDIUM"))
                            .resources(resources)
                            .build());
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse learning plan JSON: {}", json, e);
            throw new BadRequestException("AI returned invalid learning plan JSON");
        }
    }

    // ------------------------------------------------------------------
    // LOW-LEVEL
    // ------------------------------------------------------------------
    @Override
    public ChatCompletionResponse rawChat(List<ChatMessage> messages, double temperature, int maxTokens) {
        return client.chatJson(messages, temperature, maxTokens);
    }

    @Override
    public Map<String, Object> lastUsageMetadata() {
        Map<String, Object> m = lastUsage.get();
        return m == null ? Map.of() : m;
    }

    private String callJson(List<ChatMessage> messages, double temperature, int maxTokens) {
        ChatCompletionResponse response = client.chatJson(messages, temperature, maxTokens);

        Map<String, Object> usage = new HashMap<>();
        if (response.getUsage() != null) {
            usage.put("promptTokens", response.getUsage().getPromptTokens());
            usage.put("completionTokens", response.getUsage().getCompletionTokens());
            usage.put("totalTokens", response.getUsage().getTotalTokens());
        }
        usage.put("model", response.getModel());
        lastUsage.set(usage);

        String content = response.firstContent();
        if (content == null) {
            throw new BadRequestException("AI returned empty content");
        }
        return stripJsonFence(content);
    }

    private String stripJsonFence(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }

    private String safeWrite(Object value) {
        if (value == null) return "null";
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }

    private List<String> toStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(n -> list.add(n.asText()));
        }
        return list;
    }
}