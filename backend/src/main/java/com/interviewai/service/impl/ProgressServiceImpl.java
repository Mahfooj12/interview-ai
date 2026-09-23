package com.interviewai.service.impl;

import com.interviewai.dto.progress.ProgressPointResponse;
import com.interviewai.dto.progress.ProgressSkillResponse;
import com.interviewai.dto.progress.ProgressSummaryResponse;
import com.interviewai.model.Evaluation;
import com.interviewai.model.InterviewSession;
import com.interviewai.model.Report;
import com.interviewai.repository.EvaluationRepository;
import com.interviewai.repository.InterviewSessionRepository;
import com.interviewai.repository.ReportRepository;
import com.interviewai.service.ProgressService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProgressServiceImpl implements ProgressService {

    private final InterviewSessionRepository interviewRepository;
    private final ReportRepository reportRepository;
    private final EvaluationRepository evaluationRepository;

    public ProgressServiceImpl(InterviewSessionRepository interviewRepository,
                               ReportRepository reportRepository,
                               EvaluationRepository evaluationRepository) {
        this.interviewRepository = interviewRepository;
        this.reportRepository = reportRepository;
        this.evaluationRepository = evaluationRepository;
    }

    @Override
    public ProgressSummaryResponse summary(String userId) {
        List<InterviewSession> sessions =
                interviewRepository.findAllByUserIdOrderByStartedAtDesc(userId);

        long total = sessions.size();
        long completed = sessions.stream()
                .filter(s -> s.getStatus() == InterviewSession.Status.COMPLETED)
                .count();

        List<Report> reports =
                reportRepository.findAllByUserIdOrderByGeneratedAtDesc(userId);

        double avgScore = reports.stream()
                .mapToDouble(r -> r.getSections().getOverall())
                .average().orElse(0);
        double bestScore = reports.stream()
                .mapToDouble(r -> r.getSections().getOverall())
                .max().orElse(0);
        double avgTech = reports.stream()
                .mapToDouble(r -> r.getSections().getTechnical())
                .average().orElse(0);
        double avgComm = reports.stream()
                .mapToDouble(r -> r.getSections().getCommunication())
                .average().orElse(0);
        double avgConf = reports.stream()
                .mapToDouble(r -> r.getSections().getConfidence())
                .average().orElse(0);
        double avgPs = reports.stream()
                .mapToDouble(r -> r.getSections().getProblemSolving())
                .average().orElse(0);

        // Timeline from completed sessions
        List<ProgressPointResponse> timeline = new ArrayList<>();
        List<InterviewSession> completedSessions = sessions.stream()
                .filter(s -> s.getStatus() == InterviewSession.Status.COMPLETED
                        && s.getCompletedAt() != null)
                .sorted(Comparator.comparing(InterviewSession::getCompletedAt))
                .toList();
        for (InterviewSession s : completedSessions) {
            Report r = reportRepository.findByInterviewId(s.getId()).orElse(null);
            if (r == null) continue;
            timeline.add(ProgressPointResponse.builder()
                    .date(s.getCompletedAt())
                    .overallScore(r.getSections().getOverall())
                    .technicalScore(r.getSections().getTechnical())
                    .communicationScore(r.getSections().getCommunication())
                    .confidenceScore(r.getSections().getConfidence())
                    .interviewsCompleted(1)
                    .build());
        }

        // Strong/weak aggregation
        Map<String, Integer> strongCounts = new HashMap<>();
        Map<String, Integer> weakCounts = new HashMap<>();
        for (Report r : reports) {
            if (r.getStrongAreas() != null) {
                r.getStrongAreas().forEach(a -> strongCounts.merge(a, 1, Integer::sum));
            }
            if (r.getWeakAreas() != null) {
                r.getWeakAreas().forEach(a -> weakCounts.merge(a, 1, Integer::sum));
            }
        }

        List<String> topStrong = strongCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(8).map(Map.Entry::getKey).toList();
        List<String> topWeak = weakCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(8).map(Map.Entry::getKey).toList();

        return ProgressSummaryResponse.builder()
                .totalInterviews(total)
                .completedInterviews(completed)
                .averageScore(round(avgScore))
                .bestScore(round(bestScore))
                .averageTechnical(round(avgTech))
                .averageCommunication(round(avgComm))
                .averageConfidence(round(avgConf))
                .averageProblemSolving(round(avgPs))
                .topStrongAreas(topStrong)
                .topWeakAreas(topWeak)
                .timeline(timeline)
                .build();
    }

    @Override
    public List<ProgressSkillResponse> skills(String userId) {
        List<InterviewSession> sessions =
                interviewRepository.findAllByUserIdOrderByStartedAtDesc(userId);

        Map<String, double[]> aggregates = new HashMap<>(); // skill -> [sum, count]

        for (InterviewSession s : sessions) {
            List<Evaluation> evals =
                    evaluationRepository.findAllByInterviewIdOrderByCreatedAtAsc(s.getId());
            for (Evaluation e : evals) {
                if (e.getStrengths() != null) {
                    for (String strength : e.getStrengths()) {
                        double[] agg = aggregates.computeIfAbsent(strength, k -> new double[2]);
                        agg[0] += e.getOverall();
                        agg[1] += 1;
                    }
                }
            }
        }

        return aggregates.entrySet().stream()
                .map(entry -> ProgressSkillResponse.builder()
                        .skill(entry.getKey())
                        .count((int) entry.getValue()[1])
                        .averageScore(round(entry.getValue()[0] / entry.getValue()[1]))
                        .build())
                .sorted(Comparator.comparingInt(ProgressSkillResponse::getCount).reversed())
                .limit(12)
                .toList();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
