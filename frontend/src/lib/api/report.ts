import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type {
  ProgressSkill,
  ProgressSummary,
  ReportResponse,
  ReportSummary,
} from "@/types/report";

export const reportApi = {
  async list(): Promise<ReportSummary[]> {
    const { data } =
      await apiClient.get<ApiResponse<ReportSummary[]>>("/api/reports");
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