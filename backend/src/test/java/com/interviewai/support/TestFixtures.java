package com.interviewai.support;

import com.interviewai.model.InterviewSession;
import com.interviewai.model.Resume;
import com.interviewai.model.User;
import com.interviewai.model.embedded.InterviewState;
import com.interviewai.model.embedded.ParsedResume;
import com.interviewai.util.Constants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class TestFixtures {

    private TestFixtures() {}

    public static User user(String id, String email) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email(email)
                .passwordHash("$2a$12$abcdefghijklmnopqrstuv")
                .roles(Set.of(Constants.ROLE_USER))
                .plan("FREE")
                .status("ACTIVE")
                .build();
    }

    public static User admin(String id, String email) {
        return User.builder()
                .id(id)
                .name("Admin")
                .email(email)
                .passwordHash("$2a$12$abcdefghijklmnopqrstuv")
                .roles(Set.of(Constants.ROLE_USER, Constants.ROLE_ADMIN))
                .plan("PRO")
                .status("ACTIVE")
                .build();
    }

    public static Resume resume(String id, String userId) {
        return Resume.builder()
                .id(id)
                .userId(userId)
                .fileName("resume.pdf")
                .fileType("PDF")
                .fileUrl("https://example.com/resume.pdf")
                .rawText("Jane Doe — Java, Spring Boot, MongoDB")
                .parsed(ParsedResume.builder()
                        .name("Jane Doe")
                        .email("jane@example.com")
                        .skills(new ArrayList<>(List.of("Java", "Spring Boot", "MongoDB")))
                        .build())
                .primaryResume(true)
                .version(1)
                .build();
    }

    public static InterviewSession interview(String id, String userId, String resumeId) {
        return InterviewSession.builder()
                .id(id)
                .userId(userId)
                .resumeId(resumeId)
                .type(InterviewSession.Type.TECHNICAL)
                .mode(InterviewSession.Mode.TEXT)
                .difficulty(InterviewSession.Difficulty.INTERMEDIATE)
                .personality(InterviewSession.Personality.PROFESSIONAL)
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
                .build();
    }
}