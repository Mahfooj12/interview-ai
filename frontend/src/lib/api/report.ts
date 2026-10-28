import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type { PageResponse, PaginationParams } from "@/types/pagination";
import type {
  ProgressSkill,
  ProgressSummary,
  ReportResponse,
  ReportSummary,
} from "@/types/report";

export const reportApi = {
  async list(params: PaginationParams = {}): Promise<PageResponse<ReportSummary>> {
    const { data } = await apiClient.get<ApiResponse<PageResponse<ReportSummary>>>(
      "/api/reports",
      {
        params: {
          page: params.page ?? 0,
          size: params.size ?? 20,
          sort: params.sort ?? "generatedAt,desc",
        },
      },
    );
    return data.data;
  },

  async get(id: string): Promise<ReportResponse> {
    const { data } = await apiClient.get<ApiResponse<ReportResponse>>(
      `/api/reports/${id}`,
    );
    return data.data;
  },

  async getByInterview(interviewId: string): Promise<ReportResponse> {
    const { data } = await apiClient.get<ApiResponse<ReportResponse>>(
      `/api/reports/by-interview/${interviewId}`,
    );
    return data.data;
  },

  async generate(interviewId: string, force = false): Promise<{ reportId: string }> {
    const { data } = await apiClient.post<ApiResponse<{ reportId: string }>>(
      `/api/reports/generate/${interviewId}`,
      null,
      { params: { force } },
    );
    return data.data;
  },

  async progress(): Promise<ProgressSummary> {
    const { data } =
      await apiClient.get<ApiResponse<ProgressSummary>>("/api/progress");
    return data.data;
  },

  async progressSkills(): Promise<ProgressSkill[]> {
    const { data } =
      await apiClient.get<ApiResponse<ProgressSkill[]>>("/api/progress/skills");
    return data.data;
  },
};