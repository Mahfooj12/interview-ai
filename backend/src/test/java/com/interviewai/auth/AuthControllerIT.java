package com.interviewai.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.dto.auth.LoginRequest;
import com.interviewai.dto.auth.RegisterRequest;
import com.interviewai.repository.UserRepository;
import com.interviewai.support.JwtTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

//import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtTestUtils jwtTestUtils;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void registerAndLoginFlow() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .name("IT User").email("it@example.com").password("hunter2hunter2").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tokens.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("it@example.com"));

        LoginRequest login = LoginRequest.builder()
                .email("it@example.com").password("hunter2hunter2").build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokens.refreshToken").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .name("IT User").email("it2@example.com").password("hunter2hunter2").build();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = LoginRequest.builder()
                .email("it2@example.com").password("wrong-password").build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"));
    }

    @Test
    void registerWithInvalidEmailReturns400() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .name("Bad").email("not-an-email").password("hunter2hunter2").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void meRequiresAuthentication() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meReturnsUserWithValidToken() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .name("IT User").email("it3@example.com").password("hunter2hunter2").build();
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andReturn().getResponse().getContentAsString();
        String userId = objectMapper.readTree(body).path("data").path("user").path("id").asText();

        String token = jwtTestUtils.accessToken(userId, "it3@example.com", java.util.Set.of("USER"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("it3@example.com"));
    }
}