package com.interviewai.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.ai.AIService;
//import com.interviewai.model.InterviewSession;
import com.interviewai.model.Resume;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.ResumeRepository;
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

import java.util.ArrayList;
//import java.util.List;
import java.util.Map;

//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InterviewControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InterviewSessionRepository interviewRepository;
    @Autowired private ResumeRepository resumeRepository;
    @MockBean private AIService aiService;

    @BeforeEach
    void clean() {
        interviewRepository.deleteAll();
        resumeRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser(id = "u-it", email = "u-it@example.com")
    void createInterview_persistsAndReturnsSession() throws Exception {
        Resume resume = Resume.builder()
                .id("r-it").userId("u-it").primaryResume(true)
                .fileName("resume.pdf").fileType("PDF")
                .parsed(ParsedResume.builder().skills(new ArrayList<>()).build())
                .build();
        resumeRepository.save(resume);

        String payload = objectMapper.writeValueAsString(Map.of(
                "type", "TECHNICAL",
                "mode", "TEXT",
                "difficulty", "INTERMEDIATE",
                "personality", "PROFESSIONAL"));

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.data.type").value("TECHNICAL"));
    }

    @Test
    void createInterview_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}