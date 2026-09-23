package com.interviewai.admin;

import com.interviewai.dto.admin.AdminStatisticsResponse;
import com.interviewai.dto.admin.AdminUserResponse;
import com.interviewai.dto.admin.AdminUserUpdateRequest;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.model.AiUsage;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.User;
import com.interviewai.repository.AiUsageRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.ReportRepository;
import com.interviewai.repository.UserRepository;
import com.interviewai.service.impl.AdminServiceImpl;
import com.interviewai.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
//import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private InterviewSessionRepository interviewRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private AiUsageRepository aiUsageRepository;

    private AdminServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminServiceImpl(userRepository, interviewRepository, reportRepository, aiUsageRepository);
    }

    @Test
    void statistics_aggregatesCounts() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByStatus("ACTIVE")).thenReturn(8L);
        when(interviewRepository.count()).thenReturn(40L);
        when(interviewRepository.countByStatus(InterviewSession.Status.COMPLETED)).thenReturn(30L);
        when(interviewRepository.countByStatus(InterviewSession.Status.IN_PROGRESS)).thenReturn(4L);
        when(reportRepository.count()).thenReturn(25L);

        Instant now = Instant.now();
        AiUsage u1 = AiUsage.builder()
                .userId("u-1").operation(AiUsage.Operation.ANALYZE_RESUME)
                .model("deepseek-chat").promptTokens(100).completionTokens(50).totalTokens(150)
                .latencyMs(1200).success(true).createdAt(now.minusSeconds(3600)).build();
        AiUsage u2 = AiUsage.builder()
                .userId("u-2").operation(AiUsage.Operation.GENERATE_QUESTION)
                .model("deepseek-chat").promptTokens(80).completionTokens(60).totalTokens(140)
                .latencyMs(900).success(true).createdAt(now.minusSeconds(7200)).build();
        when(aiUsageRepository.findAllByCreatedAtAfter(any(Instant.class)))
                .thenReturn(List.of(u1, u2));

        AdminStatisticsResponse stats = service.statistics();

        assertThat(stats.getTotalUsers()).isEqualTo(10);
        assertThat(stats.getSuspendedUsers()).isEqualTo(2);
        assertThat(stats.getTotalInterviews()).isEqualTo(40);
        assertThat(stats.getTotalReports()).isEqualTo(25);
        assertThat(stats.getTotalAiCalls24h()).isEqualTo(2);
        assertThat(stats.getTotalTokens30d()).isEqualTo(290);
        assertThat(stats.getTopOperations()).hasSize(2);
    }

    @Test
    void updateUser_changesStatus() {
        User user = TestFixtures.user("u-1", "u@example.com");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interviewRepository.countByUserId(anyString())).thenReturn(0L);
        when(reportRepository.countByUserId(anyString())).thenReturn(0L);

        AdminUserUpdateRequest req = AdminUserUpdateRequest.builder()
                .status("SUSPENDED").build();

        AdminUserResponse response = service.updateUser("u-1", req);

        assertThat(response.getStatus()).isEqualTo("SUSPENDED");
    }

    @Test
    void updateUser_rejectsInvalidStatus() {
        when(userRepository.findById("u-1")).thenReturn(Optional.of(TestFixtures.user("u-1", "u@example.com")));
        AdminUserUpdateRequest req = AdminUserUpdateRequest.builder().status("BANANA").build();
        assertThatThrownBy(() -> service.updateUser("u-1", req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateUser_throwsWhenMissing() {
        when(userRepository.findById("u-x")).thenReturn(Optional.empty());
        AdminUserUpdateRequest req = AdminUserUpdateRequest.builder().status("ACTIVE").build();
        assertThatThrownBy(() -> service.updateUser("u-x", req))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}