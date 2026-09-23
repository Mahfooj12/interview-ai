"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { adminApi, type AdminUserUpdateRequest } from "@/lib/api/admin";
import { extractApiError } from "@/lib/api/client";

export const adminKeys = {
  statistics: ["admin", "statistics"] as const,
  users: ["admin", "users"] as const,
  interviews: (limit: number) => ["admin", "interviews", limit] as const,
  aiUsage: (limit: number) => ["admin", "ai-usage", limit] as const,
};

export function useAdminStatistics() {
  return useQuery({
    queryKey: adminKeys.statistics,
    queryFn: adminApi.statistics,
  });
}

export function useAdminUsers() {
  return useQuery({
    queryKey: adminKeys.users,
    queryFn: adminApi.users,
  });
}

export function useUpdateAdminUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: AdminUserUpdateRequest }) =>
      adminApi.updateUser(id, payload),
    onSuccess: () => {
      toast.success("User updated");
      qc.invalidateQueries({ queryKey: adminKeys.users });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useAdminInterviews(limit = 50) {
  return useQuery({
    queryKey: adminKeys.interviews(limit),
    queryFn: () => adminApi.interviews(limit),
  });
}

export function useAdminAiUsage(limit = 100) {
  return useQuery({
    queryKey: adminKeys.aiUsage(limit),
    queryFn: () => adminApi.aiUsage(limit),
  });
}