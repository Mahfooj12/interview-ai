"use client";

import { useQuery } from "@tanstack/react-query";
import { interviewApi } from "@/lib/api/interview";

export const interviewKeys = {
  all: ["interviews"] as const,
};

export function useInterviews() {
  return useQuery({
    queryKey: interviewKeys.all,
    queryFn: interviewApi.list,
  });
}