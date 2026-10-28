import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type { PageResponse, PaginationParams } from "@/types/pagination";
import type {
  InterviewDifficulty,
  InterviewMode,
  InterviewStatus,
  InterviewType,
} from "@/types/interview";

export interface AdminUser {
  id: string;
  name: string;
  email: string;
  roles: string[];
  plan: string;
  status: string;
  lastLoginAt: string | null;
  createdAt: string;
  interviewCount: number;
  reportCount: number;
}

export interface AdminUserUpdateRequest {
  status: string;
  plan?: string;
  roles?: string[];
}

export interface AdminInterview {
  id: string;
  userId: string;
  userEmail: string | null;
  type: InterviewType;
  mode: InterviewMode;
  difficulty: InterviewDifficulty;
  status: InterviewStatus;
  totalQuestionsAsked: number;
  cumulativeScore: number;
  startedAt: string | null;
  completedAt: string | null;
  durationSec: number;
}

export interface AdminAiUsage {
  id: string;
  userId: string;
  operation: string;
  model: string;
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
  latencyMs: number;
  success: boolean;
  errorMessage: string | null;
  createdAt: string;
}

export interface AdminStatistics {
  totalUsers: number;
  activeUsers: number;
  suspendedUsers: number;
  totalInterviews: number;
  completedInterviews: number;
  inProgressInterviews: number;
  totalReports: number;
  totalAiCalls24h: number;
  totalAiCalls7d: number;
  totalAiCalls30d: number;
  totalTokens30d: number;
  averageLatencyMs: number;
  topOperations: { operation: string; count: number }[];
}

export const adminApi = {
  async statistics(): Promise<AdminStatistics> {
    const { data } = await apiClient.get<ApiResponse<AdminStatistics>>(
      "/api/admin/statistics",
    );
    return data.data;
  },

  async users(params: PaginationParams = {}): Promise<PageResponse<AdminUser>> {
    const { data } = await apiClient.get<ApiResponse<PageResponse<AdminUser>>>(
      "/api/admin/users",
      {
        params: {
          page: params.page ?? 0,
          size: params.size ?? 20,
          sort: params.sort ?? "createdAt,desc",
        },
      },
    );
    return data.data;
  },

  async updateUser(
    id: string,
    payload: AdminUserUpdateRequest,
  ): Promise<AdminUser> {
    const { data } = await apiClient.put<ApiResponse<AdminUser>>(
      `/api/admin/users/${id}`,
      payload,
    );
    return data.data;
  },

  async interviews(params: PaginationParams = {}): Promise<PageResponse<AdminInterview>> {
    const { data } = await apiClient.get<ApiResponse<PageResponse<AdminInterview>>>(
      "/api/admin/interviews",
      {
        params: {
          page: params.page ?? 0,
          size: params.size ?? 50,
          sort: params.sort ?? "startedAt,desc",
        },
      },
    );
    return data.data;
  },

  async aiUsage(params: PaginationParams = {}): Promise<PageResponse<AdminAiUsage>> {
    const { data } = await apiClient.get<ApiResponse<PageResponse<AdminAiUsage>>>(
      "/api/admin/ai-usage",
      {
        params: {
          page: params.page ?? 0,
          size: params.size ?? 50,
          sort: params.sort ?? "createdAt,desc",
        },
      },
    );
    return data.data;
  },
};