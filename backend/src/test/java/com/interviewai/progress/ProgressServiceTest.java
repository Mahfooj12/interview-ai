package com.interviewai.progress;

import com.interviewai.dto.progress.ProgressSkillResponse;
import com.interviewai.dto.progress.ProgressSummaryResponse;
import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Report;
import com.interviewai.model.embedded.ReportSections;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.ReportRepository;
import com.interviewai.service.impl.ProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock private InterviewSessionRepository interviewRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private EvaluationRepository evaluationRepository;

    private ProgressServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProgressServiceImpl(interviewRepository, reportRepository, evaluationRepository);
    }

    @Test
    void summary_aggregatesReportsAndTimeline() {
        InterviewSession completed = InterviewSession.builder()
                .id("i-1").userId("u-1").status(InterviewSession.Status.COMPLETED)
                .completedAt(Instant.now()).startedAt(Instant.now().minusSeconds(600)).build();
        when(interviewRepository.findAllByUserIdOrderByStartedAtDesc("u-1"))
                .thenReturn(List.of(completed));

        Report report = Report.builder()
                .id("rep-1").userId("u-1").interviewId("i-1")
                .sections(ReportSections.builder()
                        .overall(80).technical(85).communication(72).confidence(70).problemSolving(82).build())
                .strongAreas(List.of("Java")).weakAreas(List.of("System design"))
                .generatedAt(Instant.now()).build();
        when(reportRepository.findAllByUserIdOrderByGeneratedAtDesc("u-1"))
                .thenReturn(List.of(report));
        when(reportRepository.findByInterviewId("i-1")).thenReturn(Optional.of(report));

        ProgressSummaryResponse response = service.summary("u-1");

        assertThat(response.getTotalInterviews()).isEqualTo(1);
        assertThat(response.getCompletedInterviews()).isEqualTo(1);
        assertThat(response.getAverageScore()).isEqualTo(80.0);
        assertThat(response.getBestScore()).isEqualTo(80.0);
        assertThat(response.getTopStrongAreas()).containsExactly("Java");
        assertThat(response.getTopWeakAreas()).containsExactly("System design");
        assertThat(response.getTimeline()).hasSize(1);
    }

    @Test
    void skills_aggregatesStrengthsAcrossEvaluations() {
        InterviewSession s = InterviewSession.builder().id("i-1").userId("u-1").build();
        when(interviewRepository.findAllByUserIdOrderByStartedAtDesc("u-1")).thenReturn(List.of(s));

        Evaluation eval = Evaluation.builder()
                .interviewId("i-1").overall(80).strengths(List.of("Java", "Spring"))
                .createdAt(Instant.now()).build();
        when(evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc("i-1"))
                .thenReturn(List.of(eval));

        List<ProgressSkillResponse> skills = service.skills("u-1");

        assertThat(skills).extracting(ProgressSkillResponse::getSkill)
                .contains("Java", "Spring");
    }
}