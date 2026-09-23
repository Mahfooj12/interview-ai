"use client";

import { create } from "zustand";
import type { UserResponse } from "@/lib/api/types";
import { tokenStore } from "@/lib/auth/token-store";

interface AuthState {
  user: UserResponse | null;
  hydrated: boolean;
  setUser: (user: UserResponse | null) => void;
  setTokens: (accessToken: string, refreshToken: string) => void;
  hydrate: () => void;
  clear: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  hydrated: false,

  setUser: (user) => {
    tokenStore.setUser(user);
    set({ user });
  },

  setTokens: (accessToken, refreshToken) => {
    tokenStore.setTokens(accessToken, refreshToken);
  },

  hydrate: () => {
    const user = tokenStore.getUser<UserResponse>();
    set({ user, hydrated: true });
  },

  clear: () => {
    tokenStore.clear();
    set({ user: null });
  },
}));