import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type {
  JobCreateTextPayload,
  JobDescriptionResponse,
  JobDescriptionSummary,
  JobMatchResponse,
} from "@/types/job";

export const jobApi = {
  async list(): Promise<JobDescriptionSummary[]> {
    const { data } =
      await apiClient.get<ApiResponse<JobDescriptionSummary[]>>("/api/jobs");
    return data.data;
  },

  async get(id: string): Promise<JobDescriptionResponse> {
    const { data } = await apiClient.get<ApiResponse<JobDescriptionResponse>>(
      `/api/jobs/${id}`,
    );
    return data.data;
  },

  async createFromText(
    payload: JobCreateTextPayload,
  ): Promise<JobDescriptionResponse> {
    const { data } = await apiClient.post<ApiResponse<JobDescriptionResponse>>(
      "/api/jobs",
      payload,
    );
    return data.data;
  },

  async createFromFile(
    file: File,
    resumeId?: string,
  ): Promise<JobDescriptionResponse> {
    const formData = new FormData();
    formData.append("file", file);
    const { data } = await apiClient.post<ApiResponse<JobDescriptionResponse>>(
      "/api/jobs/upload",
      formData,
      {
        params: { resumeId },
        headers: { "Content-Type": "multipart/form-data" },
      },
    );
    return data.data;
  },

  async match(id: string, resumeId: string): Promise<JobMatchResponse> {
    const { data } = await apiClient.post<ApiResponse<JobMatchResponse>>(
      `/api/jobs/${id}/match`,
      null,
      { params: { resumeId } },
    );
    return data.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/api/jobs/${id}`);
  },
};