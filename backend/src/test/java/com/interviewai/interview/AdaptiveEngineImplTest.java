package com.interviewai.interview;

import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Question;
import com.interviewai.model.embedded.InterviewState;
import com.interviewai.service.AdaptiveEngine;
import com.interviewai.service.impl.AdaptiveEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdaptiveEngineImplTest {

    private AdaptiveEngineImpl engine;

    @BeforeEach
    void setUp() {
        engine = new AdaptiveEngineImpl();
        ReflectionTestUtils.setField(engine, "maxQuestions", 5);
    }

    @Test
    void firstQuestionDoesNotEndInterview() {
        InterviewSession session = newSession(InterviewSession.Difficulty.INTERMEDIATE);
        AdaptiveEngine.Decision d = engine.decideNext(session, null, List.of());
        assertThat(d.endInterview()).isFalse();
        assertThat(d.followUp()).isFalse();
    }

    @Test
    void strongAnswerEscalatesDifficulty() {
        InterviewSession session = newSession(InterviewSession.Difficulty.BEGINNER);
        Evaluation eval = Evaluation.builder().overall(85).build();
        assertThat(engine.nextDifficulty(session.getDifficulty(), eval))
                .isEqualTo(InterviewSession.Difficulty.INTERMEDIATE);
    }

    @Test
    void weakAnswerDowngradesDifficulty() {
        InterviewSession session = newSession(InterviewSession.Difficulty.INTERMEDIATE);
        Evaluation eval = Evaluation.builder().overall(40).build();
        assertThat(engine.nextDifficulty(session.getDifficulty(), eval))
                .isEqualTo(InterviewSession.Difficulty.BEGINNER);
    }

    @Test
    void maxQuestionsEndsInterview() {
        InterviewSession session = newSession(InterviewSession.Difficulty.INTERMEDIATE);
        List<Question> asked = new ArrayList<>();
        for (int i = 0; i < 5; i++) asked.add(Question.builder().id("q-" + i).build());

        AdaptiveEngine.Decision d = engine.decideNext(session, Evaluation.builder().overall(70).build(), asked);
        assertThat(d.endInterview()).isTrue();
    }

    @Test
    void updateState_accumulatesWeakAndStrongAreas() {
        InterviewSession session = newSession(InterviewSession.Difficulty.INTERMEDIATE);
        Evaluation eval = Evaluation.builder()
                .overall(80)
                .weakAreasDetected(List.of("depth"))
                .strengths(List.of("clarity"))
                .build();

        engine.updateState(session, eval);

        assertThat(session.getState().getTotalQuestionsAsked()).isEqualTo(1);
        assertThat(session.getState().getWeakAreas()).containsExactly("depth");
        assertThat(session.getState().getStrongAreas()).containsExactly("clarity");
        assertThat(session.getState().getCumulativeScore()).isEqualTo(80.0);
    }

    @Test
    void followUpRequestedWhenWeakAnswerAndThresholdNotMet() {
        InterviewSession session = newSession(InterviewSession.Difficulty.INTERMEDIATE);
        List<Question> asked = List.of(Question.builder().id("q-1").build());
        Evaluation eval = Evaluation.builder().overall(55).followUpNeeded(true).build();

        AdaptiveEngine.Decision d = engine.decideNext(session, eval, asked);
        assertThat(d.followUp()).isTrue();
    }

    private InterviewSession newSession(InterviewSession.Difficulty diff) {
        return InterviewSession.builder()
                .id("i-1").userId("u-1").resumeId("r-1")
                .type(InterviewSession.Type.TECHNICAL)
                .mode(InterviewSession.Mode.TEXT)
                .difficulty(diff)
                .personality(InterviewSession.Personality.PROFESSIONAL)
                .status(InterviewSession.Status.IN_PROGRESS)
                .state(InterviewState.builder()
                        .weakAreas(new ArrayList<>())
                        .strongAreas(new ArrayList<>())
                        .build())
                .build();
    }
}