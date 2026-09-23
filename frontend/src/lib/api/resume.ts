import { apiClient } from "@/lib/api/client";
import type {
  ApiResponse,
} from "@/lib/api/types";
import type {
  ResumeAnalysisResponse,
  ResumeResponse,
  ResumeSummary,
  ResumeUploadResponse,
} from "@/types/resume";

export const resumeApi = {
  async list(): Promise<ResumeSummary[]> {
    const { data } =
      await apiClient.get<ApiResponse<ResumeSummary[]>>("/api/resumes");
    return data.data;
  },

  async get(id: string): Promise<ResumeResponse> {
    const { data } = await apiClient.get<ApiResponse<ResumeResponse>>(
      `/api/resumes/${id}`,
    );
    return data.data;
  },

  async upload(file: File): Promise<ResumeUploadResponse> {
    const formData = new FormData();
    formData.append("file", file);
    const { data } = await apiClient.post<ApiResponse<ResumeUploadResponse>>(
      "/api/resumes",
      formData,
      { headers: { "Content-Type": "multipart/form-data" } },
    );
    return data.data;
  },

  async analyze(id: string, force = false): Promise<ResumeAnalysisResponse> {
    const { data } = await apiClient.post<ApiResponse<ResumeAnalysisResponse>>(
      `/api/resumes/${id}/analyze`,
      null,
      { params: { force } },
    );
    return data.data;
  },

  async setPrimary(id: string): Promise<ResumeResponse> {
    const { data } = await apiClient.post<ApiResponse<ResumeResponse>>(
      `/api/resumes/${id}/primary`,
    );
    return data.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/api/resumes/${id}`);
  },
};