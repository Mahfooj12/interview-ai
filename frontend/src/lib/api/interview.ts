import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type {
  AnswerRequest,
  AnswerResponse,
  CreateInterviewRequest,
  CursorPage,
  InterviewResponse,
  InterviewSummary,
  NextQuestionResponse,
} from "@/types/interview";

export const interviewApi = {
  async list(): Promise<InterviewSummary[]> {
    const { data } =
      await apiClient.get<ApiResponse<InterviewSummary[]>>("/api/interviews");
    return data.data;
  },
  // Append inside the interviewApi object:

  async listPaginated(params?: {
    limit?: number;
    cursor?: string | null;
  }): Promise<CursorPage<InterviewSummary>> {
    const { data } = await apiClient.get<
      ApiResponse<CursorPage<InterviewSummary>>
    >("/api/interviews", {
      params: {
        limit: params?.limit,
        cursor: params?.cursor ?? undefined,
      },
    });
    return data.data;
  },

  async create(payload: CreateInterviewRequest): Promise<InterviewResponse> {
    const { data } = await apiClient.post<ApiResponse<InterviewResponse>>(
      "/api/interviews",
      payload,
    );
    return data.data;
  },

  async get(id: string): Promise<InterviewResponse> {
    const { data } = await apiClient.get<ApiResponse<InterviewResponse>>(
      `/api/interviews/${id}`,
    );
    return data.data;
  },

  async start(id: string): Promise<NextQuestionResponse> {
    const { data } = await apiClient.post<ApiResponse<NextQuestionResponse>>(
      `/api/interviews/${id}/start`,
    );
    return data.data;
  },

  async submitAnswer(
    id: string,
    payload: AnswerRequest,
  ): Promise<AnswerResponse> {
    const { data } = await apiClient.post<ApiResponse<AnswerResponse>>(
      `/api/interviews/${id}/answer`,
      payload,
    );
    return data.data;
  },

  async nextQuestion(id: string): Promise<NextQuestionResponse> {
    const { data } = await apiClient.post<ApiResponse<NextQuestionResponse>>(
      `/api/interviews/${id}/next-question`,
    );
    return data.data;
  },

  async complete(id: string): Promise<void> {
    await apiClient.post(`/api/interviews/${id}/complete`);
  },
};