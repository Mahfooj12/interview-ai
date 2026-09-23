"use client";

import {
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import { toast } from "sonner";
import { interviewApi } from "@/lib/api/interview";
import { extractApiError } from "@/lib/api/client";
import type {
  AnswerRequest,
  CreateInterviewRequest,
  NextQuestionResponse,
} from "@/types/interview";

export const interviewKeys = {
  all: ["interviews"] as const,
  detail: (id: string) => ["interviews", id] as const,
};

export function useInterviews() {
  return useQuery({
    queryKey: interviewKeys.all,
    queryFn: interviewApi.list,
  });
}

export function useInterview(id: string | undefined) {
  return useQuery({
    queryKey: interviewKeys.detail(id ?? ""),
    queryFn: () => interviewApi.get(id as string),
    enabled: !!id,
  });
}

export function useCreateInterview() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateInterviewRequest) =>
      interviewApi.create(payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: interviewKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useStartInterview() {
  return useMutation({
    mutationFn: (id: string) => interviewApi.start(id),
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useSubmitAnswer(id: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: AnswerRequest) =>
      interviewApi.submitAnswer(id, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: interviewKeys.detail(id) });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useNextQuestion(id: string) {
  return useMutation({
    mutationFn: () => interviewApi.nextQuestion(id),
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useCompleteInterview(id: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => interviewApi.complete(id),
    onSuccess: () => {
      toast.success("Interview completed");
      qc.invalidateQueries({ queryKey: interviewKeys.detail(id) });
      qc.invalidateQueries({ queryKey: interviewKeys.all });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export type { NextQuestionResponse };