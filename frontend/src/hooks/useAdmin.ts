"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { adminApi, type AdminUserUpdateRequest } from "@/lib/api/admin";
import { extractApiError } from "@/lib/api/client";

export const adminKeys = {
  statistics: ["admin", "statistics"] as const,
  users: (page: number, size: number) => ["admin", "users", page, size] as const,
  interviews: (page: number, size: number) =>
    ["admin", "interviews", page, size] as const,
  aiUsage: (page: number, size: number) =>
    ["admin", "ai-usage", page, size] as const,
};

export function useAdminStatistics() {
  return useQuery({
    queryKey: adminKeys.statistics,
    queryFn: adminApi.statistics,
  });
}

export function useAdminUsers(page = 0, size = 20) {
  return useQuery({
    queryKey: adminKeys.users(page, size),
    queryFn: () => adminApi.users({ page, size }),
    placeholderData: (prev) => prev,
  });
}

export function useUpdateAdminUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: AdminUserUpdateRequest }) =>
      adminApi.updateUser(id, payload),
    onSuccess: () => {
      toast.success("User updated");
      qc.invalidateQueries({ queryKey: ["admin", "users"] });
    },
    onError: (err) => toast.error(extractApiError(err)),
  });
}

export function useAdminInterviews(page = 0, size = 50) {
  return useQuery({
    queryKey: adminKeys.interviews(page, size),
    queryFn: () => adminApi.interviews({ page, size }),
    placeholderData: (prev) => prev,
  });
}

export function useAdminAiUsage(page = 0, size = 50) {
  return useQuery({
    queryKey: adminKeys.aiUsage(page, size),
    queryFn: () => adminApi.aiUsage({ page, size }),
    placeholderData: (prev) => prev,
  });
}