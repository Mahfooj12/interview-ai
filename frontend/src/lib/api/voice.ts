import { apiClient } from "@/lib/api/client";
import type { ApiResponse } from "@/lib/api/types";
import type { SttResponse, TtsResponse } from "@/types/voice";

export const voiceApi = {
  async transcribe(audio: Blob, language = "en"): Promise<SttResponse> {
    const formData = new FormData();
    formData.append("audio", audio, "answer.webm");
    formData.append("language", language);
    const { data } = await apiClient.post<ApiResponse<SttResponse>>(
      "/api/voice/transcribe",
      formData,
      { headers: { "Content-Type": "multipart/form-data" } },
    );
    return data.data;
  },

  async synthesize(text: string, voice = "alloy"): Promise<TtsResponse> {
    const { data } = await apiClient.post<ApiResponse<TtsResponse>>(
      "/api/voice/synthesize",
      { text, voice },
    );
    return data.data;
  },
};