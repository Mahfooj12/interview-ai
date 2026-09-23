"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { reportApi } from "@/lib/api/report";
import { extractApiError } from "@/lib/api/client";

export const reportKeys = {
  all: ["reports"] as const,
  detail: (id: string) => ["reports", id] as const,
  byInterview: (interviewId: string) =>
    ["reports", "by-interview", interviewId] as const,
  progress: ["progress"] as const,
  progressSkills: ["progress", "skills"] as const,
};

export function useReports() {
  return useQuery({
    queryKey: reportKeys.all,
    queryFn: reportApi.list,
  });
}

export function useReport(id: string | undefined) {
  return useQuery({
    queryKey: reportKeys.detail(id ?? ""),
    queryFn: () => reportApi.get(id as string),
    enabled: !!id,
  });
}

export function useReportByInterview(interviewId: string | undefined) {
  return useQuery({
    queryKey: reportKeys.byInterview(interviewId ?? ""),
    queryFn: () => reportApi.getByInterview(interviewId as string),
    enabled: !!interviewId,
    retry: 2,
    retryDelay: 1000,
  });
}

export function useGenerateReport() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ interviewId, force }: { interviewId: string; force?: boolean }) =>
      reportApi.generate(interviewId, force),
    onSuccess: (_data, variables) => {
      toast.success("Report generated");
      qc.invalidateQueries({ queryKey: reportKeys.byInterview(variables.interviewId) });
      qc.invalidateQueries({ queryKey: reportKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useProgress() {
  return useQuery({
    queryKey: reportKeys.progress,
    queryFn: reportApi.progress,
  });
}

export function useProgressSkills() {
  return useQuery({
    queryKey: reportKeys.progressSkills,
    queryFn: reportApi.progressSkills,
  });
}