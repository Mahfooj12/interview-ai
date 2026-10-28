import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type { PageResponse, PaginationParams } from "@/types/pagination";
import type { EvaluationResponse } from "@/types/interview";

export const evaluationApi = {
  async listForInterview(
    interviewId: string,
    params: PaginationParams = {},
  ): Promise<PageResponse<EvaluationResponse>> {
    const { data } = await apiClient.get<
      ApiResponse<PageResponse<EvaluationResponse>>
    >(`/api/interviews/${interviewId}/evaluations`, {
      params: {
        page: params.page ?? 0,
        size: params.size ?? 20,
        sort: params.sort ?? "createdAt,asc",
      },
    });
    return data.data;
  },
};