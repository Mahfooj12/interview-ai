export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
  timestamp: string;
}

export interface ApiError {
  success: false;
  message: string;
  errorCode: string;
  timestamp: string;
  path: string;
}

export interface UserResponse {
  id: string;
  name: string;
  email: string;
  roles: string[];
  plan: string;
  avatarUrl: string | null;
  status: string;
  createdAt: string;
}

export interface TokenPair {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresInMs: number;
  refreshTokenExpiresInMs: number;
  tokenType: string;
}

export interface AuthResponse {
  user: UserResponse;
  tokens: TokenPair;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}