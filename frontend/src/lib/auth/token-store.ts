const ACCESS_TOKEN_KEY = "interviewai.accessToken";
const REFRESH_TOKEN_KEY = "interviewai.refreshToken";
const USER_KEY = "interviewai.user";

const isBrowser = typeof window !== "undefined";

export const tokenStore = {
  getAccessToken(): string | null {
    if (!isBrowser) return null;
    return window.localStorage.getItem(ACCESS_TOKEN_KEY);
  },
  getRefreshToken(): string | null {
    if (!isBrowser) return null;
    return window.localStorage.getItem(REFRESH_TOKEN_KEY);
  },
  setTokens(accessToken: string, refreshToken: string) {
    if (!isBrowser) return;
    window.localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    window.localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  },
  setUser(user: unknown) {
    if (!isBrowser) return;
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  },
  getUser<T>(): T | null {
    if (!isBrowser) return null;
    const raw = window.localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as T;
    } catch {
      return null;
    }
  },
  clear() {
    if (!isBrowser) return;
    window.localStorage.removeItem(ACCESS_TOKEN_KEY);
    window.localStorage.removeItem(REFRESH_TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
  },
};