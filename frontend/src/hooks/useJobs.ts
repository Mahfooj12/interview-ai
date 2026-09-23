"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { jobApi } from "@/lib/api/job";
import { extractApiError } from "@/lib/api/client";
import type { JobCreateTextPayload } from "@/types/job";

export const jobKeys = {
  all: ["jobs"] as const,
  detail: (id: string) => ["jobs", id] as const,
};

export function useJobs() {
  return useQuery({
    queryKey: jobKeys.all,
    queryFn: jobApi.list,
  });
}

export function useJob(id: string | undefined) {
  return useQuery({
    queryKey: jobKeys.detail(id ?? ""),
    queryFn: () => jobApi.get(id as string),
    enabled: !!id,
  });
}

export function useCreateJobFromText() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: JobCreateTextPayload) =>
      jobApi.createFromText(payload),
    onSuccess: () => {
      toast.success("Job description created");
      qc.invalidateQueries({ queryKey: jobKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useCreateJobFromFile() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ file, resumeId }: { file: File; resumeId?: string }) =>
      jobApi.createFromFile(file, resumeId),
    onSuccess: () => {
      toast.success("Job description created");
      qc.invalidateQueries({ queryKey: jobKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useMatchJob() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, resumeId }: { id: string; resumeId: string }) =>
      jobApi.match(id, resumeId),
    onSuccess: (_data, variables) => {
      toast.success("Match computed");
      qc.invalidateQueries({ queryKey: jobKeys.detail(variables.id) });
      qc.invalidateQueries({ queryKey: jobKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useDeleteJob() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => jobApi.delete(id),
    onSuccess: () => {
      toast.success("Job description deleted");
      qc.invalidateQueries({ queryKey: jobKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}