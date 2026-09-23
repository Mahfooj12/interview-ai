import axios, {
  AxiosError,
  AxiosInstance,
  InternalAxiosRequestConfig,
} from "axios";
import { tokenStore } from "@/lib/auth/token-store";
import type { ApiError, TokenPair } from "@/lib/api/types";

const API_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 180_000,
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = tokenStore.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let isRefreshing = false;
let refreshQueue: Array<(token: string) => void> = [];

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiError>) => {
    const original = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    if (
      error.response?.status === 401 &&
      original &&
      !original._retry &&
      !original.url?.includes("/api/auth/")
    ) {
      const refreshToken = tokenStore.getRefreshToken();
      if (!refreshToken) {
        tokenStore.clear();
        if (typeof window !== "undefined") window.location.href = "/login";
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve) => {
          refreshQueue.push((token) => {
            original.headers.Authorization = `Bearer ${token}`;
            resolve(apiClient(original));
          });
        });
      }

      original._retry = true;
      isRefreshing = true;

      try {
        const { data } = await axios.post<{
          success: boolean;
          data: TokenPair;
        }>(`${API_URL}/api/auth/refresh`, { refreshToken });

        const tokens = data.data;
        tokenStore.setTokens(tokens.accessToken, tokens.refreshToken);

        refreshQueue.forEach((cb) => cb(tokens.accessToken));
        refreshQueue = [];

        original.headers.Authorization = `Bearer ${tokens.accessToken}`;
        return apiClient(original);
      } catch (refreshErr) {
        tokenStore.clear();
        if (typeof window !== "undefined") window.location.href = "/login";
        return Promise.reject(refreshErr);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);

export function extractApiError(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiError | undefined;
    if (data?.message) return data.message;
    if (error.message) return error.message;
  }
  if (error instanceof Error) return error.message;
  return "Something went wrong";
}