package com.interviewai.service.impl;

import com.interviewai.dto.admin.AdminAiUsageResponse;
import com.interviewai.dto.admin.AdminInterviewResponse;
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
import com.interviewai.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final InterviewSessionRepository interviewRepository;
    private final ReportRepository reportRepository;
    private final AiUsageRepository aiUsageRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            InterviewSessionRepository interviewRepository,
                            ReportRepository reportRepository,
                            AiUsageRepository aiUsageRepository) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.reportRepository = reportRepository;
        this.aiUsageRepository = aiUsageRepository;
    }

    @Override
    public AdminStatisticsResponse statistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus("ACTIVE");
        long suspendedUsers = totalUsers - activeUsers;

        long totalInterviews = interviewRepository.count();
        long completedInterviews = interviewRepository.countByStatus(InterviewSession.Status.COMPLETED);
        long inProgressInterviews = interviewRepository.countByStatus(InterviewSession.Status.IN_PROGRESS);

        long totalReports = reportRepository.count();

        Instant now = Instant.now();
        Instant d1 = now.minus(1, ChronoUnit.DAYS);
        Instant d7 = now.minus(7, ChronoUnit.DAYS);
        Instant d30 = now.minus(30, ChronoUnit.DAYS);

        List<AiUsage> last30 = aiUsageRepository.findAllByCreatedAtAfter(d30);
        long calls24h = last30.stream().filter(a -> a.getCreatedAt().isAfter(d1)).count();
        long calls7d = last30.stream().filter(a -> a.getCreatedAt().isAfter(d7)).count();
        long calls30d = last30.size();

        long totalTokens30d = last30.stream().mapToLong(AiUsage::getTotalTokens).sum();
        double avgLatency = last30.stream()
                .filter(AiUsage::isSuccess)
                .mapToLong(AiUsage::getLatencyMs)
                .average().orElse(0);

        Map<String, Long> opCounts = new HashMap<>();
        for (AiUsage u : last30) {
            opCounts.merge(u.getOperation().name(), 1L, Long::sum);
        }
        List<AdminStatisticsResponse.OperationCount> topOperations = opCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .map(e -> AdminStatisticsResponse.OperationCount.builder()
                        .operation(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        return AdminStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .suspendedUsers(suspendedUsers)
                .totalInterviews(totalInterviews)
                .completedInterviews(completedInterviews)
                .inProgressInterviews(inProgressInterviews)
                .totalReports(totalReports)
                .totalAiCalls24h(calls24h)
                .totalAiCalls7d(calls7d)
                .totalAiCalls30d(calls30d)
                .totalTokens30d(totalTokens30d)
                .averageLatencyMs(round(avgLatency))
                .topOperations(topOperations)
                .build();
    }

    @Override
    public List<AdminUserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toAdminUser)
                .toList();
    }

    @Override
    public AdminUserResponse updateUser(String userId, AdminUserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String status = request.getStatus().toUpperCase();
        if (!"ACTIVE".equals(status) && !"SUSPENDED".equals(status)) {
            throw new BadRequestException("Invalid status");
        }
        user.setStatus(status);

        if (request.getPlan() != null && !request.getPlan().isBlank()) {
            String plan = request.getPlan().toUpperCase();
            if (!List.of("FREE", "PRO", "TEAM").contains(plan)) {
                throw new BadRequestException("Invalid plan");
            }
            user.setPlan(plan);
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        }

        userRepository.save(user);
        return toAdminUser(user);
    }

    @Override
    public List<AdminInterviewResponse> listInterviews(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return interviewRepository.findAll().stream()
                .sorted((a, b) -> {
                    Instant ai = a.getStartedAt() != null ? a.getStartedAt() : a.getCreatedAt();
                    Instant bi = b.getStartedAt() != null ? b.getStartedAt() : b.getCreatedAt();
                    if (ai == null && bi == null) return 0;
                    if (ai == null) return 1;
                    if (bi == null) return -1;
                    return bi.compareTo(ai);
                })
                .limit(safeLimit)
                .map(this::toAdminInterview)
                .toList();
    }

    @Override
    public List<AdminAiUsageResponse> listAiUsage(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        return aiUsageRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(safeLimit)
                .map(this::toAdminAiUsage)
                .toList();
    }

    private AdminUserResponse toAdminUser(User user) {
        long interviewCount = interviewRepository.countByUserId(user.getId());
        long reportCount = reportRepository.countByUserId(user.getId());
        return AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .plan(user.getPlan())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .interviewCount(interviewCount)
                .reportCount(reportCount)
                .build();
    }

    private AdminInterviewResponse toAdminInterview(InterviewSession session) {
        String email = userRepository.findById(session.getUserId())
                .map(User::getEmail)
                .orElse(null);
        return AdminInterviewResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .userEmail(email)
                .type(session.getType())
                .mode(session.getMode())
                .difficulty(session.getDifficulty())
                .status(session.getStatus())
                .totalQuestionsAsked(session.getState() != null
                        ? session.getState().getTotalQuestionsAsked() : 0)
                .cumulativeScore(session.getState() != null
                        ? session.getState().getCumulativeScore() : 0)
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .durationSec(session.getDurationSec())
                .build();
    }

    private AdminAiUsageResponse toAdminAiUsage(AiUsage usage) {
        return AdminAiUsageResponse.builder()
                .id(usage.getId())
                .userId(usage.getUserId())
                .operation(usage.getOperation())
                .model(usage.getModel())
                .promptTokens(usage.getPromptTokens())
                .completionTokens(usage.getCompletionTokens())
                .totalTokens(usage.getTotalTokens())
                .latencyMs(usage.getLatencyMs())
                .success(usage.isSuccess())
                .errorMessage(usage.getErrorMessage())
                .createdAt(usage.getCreatedAt())
                .build();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
