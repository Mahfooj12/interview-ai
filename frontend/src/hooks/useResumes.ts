"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { resumeApi } from "@/lib/api/resume";
import { extractApiError } from "@/lib/api/client";

export const resumeKeys = {
  all: ["resumes"] as const,
  detail: (id: string) => ["resumes", id] as const,
};

export function useResumes() {
  return useQuery({
    queryKey: resumeKeys.all,
    queryFn: resumeApi.list,
  });
}

export function useResume(id: string | undefined) {
  return useQuery({
    queryKey: resumeKeys.detail(id ?? ""),
    queryFn: () => resumeApi.get(id as string),
    enabled: !!id,
  });
}

export function useUploadResume() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (file: File) => resumeApi.upload(file),
    onSuccess: () => {
      toast.success("Resume uploaded");
      qc.invalidateQueries({ queryKey: resumeKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useAnalyzeResume() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, force }: { id: string; force?: boolean }) =>
      resumeApi.analyze(id, force),
    onSuccess: (_data, variables) => {
      toast.success("Resume analyzed");
      qc.invalidateQueries({ queryKey: resumeKeys.all });
      qc.invalidateQueries({ queryKey: resumeKeys.detail(variables.id) });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useSetPrimaryResume() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => resumeApi.setPrimary(id),
    onSuccess: () => {
      toast.success("Primary resume updated");
      qc.invalidateQueries({ queryKey: resumeKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useDeleteResume() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => resumeApi.delete(id),
    onSuccess: () => {
      toast.success("Resume deleted");
      qc.invalidateQueries({ queryKey: resumeKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}