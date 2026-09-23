package com.interviewai.resume;

import com.interviewai.repository.ResumeRepository;
import com.interviewai.support.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

//import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResumeControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ResumeRepository resumeRepository;

    @BeforeEach
    void clean() {
        resumeRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser(id = "user-it", email = "user-it@example.com")
    void uploadRejectsUnsupportedType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "hello".getBytes());

        mockMvc.perform(multipart("/api/resumes").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockCustomUser(id = "user-it", email = "user-it@example.com")
    void uploadWithoutAuthFails() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/resumes"))
                .andExpect(status().isUnauthorized());
    }
}