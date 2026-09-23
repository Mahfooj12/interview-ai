import { apiClient } from "@/lib/api/client";
import type {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserResponse,
} from "@/lib/api/types";

export const authApi = {
  async register(payload: RegisterRequest): Promise<AuthResponse> {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      "/api/auth/register",
      payload,
    );
    return data.data;
  },

  async login(payload: LoginRequest): Promise<AuthResponse> {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      "/api/auth/login",
      payload,
    );
    return data.data;
  },

  async me(): Promise<UserResponse> {
    const { data } =
      await apiClient.get<ApiResponse<UserResponse>>("/api/auth/me");
    return data.data;
  },

  async logout(refreshToken: string): Promise<void> {
    await apiClient.post("/api/auth/logout", { refreshToken });
  },
};