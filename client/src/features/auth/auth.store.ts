import { create } from "zustand";

const STORAGE_KEY = "it_auth_token";

type AuthState = {
  token: string | null;
  isAuthenticated: boolean;
  setToken: (token: string) => void;
  clearToken: () => void;
  hydrate: () => void;
};

export const useAuthStore = create<AuthState>((set, get) => ({
  token: null,
  isAuthenticated: false,

  setToken: (token) => {
    localStorage.setItem(STORAGE_KEY, token);
    set({ token, isAuthenticated: true });
  },

  clearToken: () => {
    localStorage.removeItem(STORAGE_KEY);
    set({ token: null, isAuthenticated: false });
  },

  hydrate: () => {
    const token = localStorage.getItem(STORAGE_KEY);
    if (token) {
      set({ token, isAuthenticated: true });
    } else {
      set({ token: null, isAuthenticated: false });
    }
  },
}));
