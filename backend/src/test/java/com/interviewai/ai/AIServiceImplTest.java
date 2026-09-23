package com.interviewai.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.client.DeepSeekClient;
import com.interviewai.ai.client.dto.ChatChoice;
import com.interviewai.ai.client.dto.ChatCompletionResponse;
import com.interviewai.ai.client.dto.ChatMessage;
import com.interviewai.ai.client.dto.ChatUsage;
import com.interviewai.ai.prompt.PromptSanitizer;
import com.interviewai.model.Evaluation;
import com.interviewai.model.embedded.LearningItem;
import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.model.embedded.ParsedResume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @Mock private DeepSeekClient client;

    private AIServiceImpl service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = new AIServiceImpl(client, new PromptSanitizer(), objectMapper);
    }

    @Test
    void analyzeResume_parsesJsonResponse() {
        String json = """
                {
                  "name": "Jane Doe",
                  "email": "jane@example.com",
                  "phone": null,
                  "summary": "Backend",
                  "skills": ["Java", "Spring Boot"],
                  "languages": [],
                  "frameworks": ["Spring"],
                  "databases": ["MongoDB"],
                  "tools": [],
                  "projects": [],
                  "experience": [],
                  "education": [],
                  "certifications": [],
                  "achievements": [],
                  "technicalClaims": []
                }
                """;
        when(client.chatJson(anyList(), anyDouble(), anyInt()))
                .thenReturn(responseWith(json));

        ParsedResume resume = service.analyzeResume("u-1", "Jane — Java, Spring Boot");
        assertThat(resume.getName()).isEqualTo("Jane Doe");
        assertThat(resume.getSkills()).contains("Java", "Spring Boot");
    }

    @Test
    void evaluateAnswer_parsesEvaluation() {
        String json = """
                {
                  "technicalAccuracy": 82,
                  "relevance": 90,
                  "depth": 70,
                  "clarity": 85,
                  "communication": 78,
                  "confidence": 72,
                  "overall": 80,
                  "strengths": ["clear"],
                  "weaknesses": ["shallow"],
                  "weakAreasDetected": ["depth"],
                  "feedback": "Good but needs depth",
                  "idealAnswer": "Explain DI...",
                  "followUpNeeded": true
                }
                """;
        when(client.chatJson(anyList(), anyDouble(), anyInt()))
                .thenReturn(responseWith(json));

        Evaluation evaluation = service.evaluateAnswer("u-1",
                new AIService.EvaluationContext(
                        "TECHNICAL", "INTERMEDIATE",
                        "What is DI?", List.of("Dependency injection"),
                        "DI is a design pattern"));

        assertThat(evaluation.getOverall()).isEqualTo(80);
        assertThat(evaluation.isFollowUpNeeded()).isTrue();
        assertThat(evaluation.getWeakAreasDetected()).containsExactly("depth");
    }

    @Test
    void matchResumeWithJob_parsesMatch() {
        String json = """
                {
                  "matchedSkills": ["Java", "Spring Boot"],
                  "partiallyMatchedSkills": ["Kafka"],
                  "missingSkills": ["Kubernetes"],
                  "matchPercentage": 72.5
                }
                """;
        when(client.chatJson(anyList(), anyDouble(), anyInt()))
                .thenReturn(responseWith(json));

        MatchResult match = service.matchResumeWithJob("u-1",
                ParsedResume.builder().skills(List.of("Java", "Spring Boot")).build(),
                ParsedJobDescription.builder().requiredSkills(List.of("Java")).build());

        assertThat(match.getMatchPercentage()).isEqualTo(72.5);
        assertThat(match.getMissingSkills()).containsExactly("Kubernetes");
    }

    @Test
    void generateLearningPlan_parsesItems() {
        String json = """
                {
                  "items": [
                    {
                      "topic": "Spring Security",
                      "priority": "HIGH",
                      "resources": [
                        {"type":"COURSE","title":"Spring Security","url":"https://x"}
                      ]
                    }
                  ]
                }
                """;
        when(client.chatJson(anyList(), anyDouble(), anyInt()))
                .thenReturn(responseWith(json));

        List<LearningItem> plan = service.generateLearningPlan("u-1",
                List.of("Spring Security"), "INTERMEDIATE");

        assertThat(plan).hasSize(1);
        assertThat(plan.get(0).getTopic()).isEqualTo("Spring Security");
        assertThat(plan.get(0).getResources()).hasSize(1);
    }

    private ChatCompletionResponse responseWith(String jsonContent) {
        ChatMessage msg = ChatMessage.builder().role("assistant").content(jsonContent).build();
        ChatChoice choice = ChatChoice.builder().index(0).message(msg).finishReason("stop").build();
        return ChatCompletionResponse.builder()
                .id("resp-1")
                .model("deepseek-chat")
                .created(System.currentTimeMillis())
                .choices(List.of(choice))
                .usage(ChatUsage.builder().promptTokens(10).completionTokens(20).totalTokens(30).build())
                .build();
    }
}