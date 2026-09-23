package com.interviewai.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.AIService;
import com.interviewai.model.embedded.ParsedJobDescription;
import com.interviewai.repository.JobDescriptionRepository;
import com.interviewai.support.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

//import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobDescriptionControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JobDescriptionRepository jobRepository;
    @MockBean private AIService aiService;

    @BeforeEach
    void clean() {
        jobRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser(id = "u-it")
    void createFromText_persistsAndReturns() throws Exception {
        when(aiService.analyzeJobDescription(anyString(), anyString()))
                .thenReturn(ParsedJobDescription.builder()
                        .title("Backend Engineer")
                        .requiredSkills(List.of("Java"))
                        .build());
        when(aiService.lastUsageMetadata()).thenReturn(Map.of());

        String payload = objectMapper.writeValueAsString(Map.of(
                "rawText", "We need a backend engineer with strong Java and Spring Boot skills."));

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.parsed.title").value("Backend Engineer"));
    }

    @Test
    void createFromText_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
