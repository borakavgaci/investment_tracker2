import { create } from "zustand";

export const STORAGE_KEY = "investment_tracker_token"; // eğer sende farklıysa burada kendi kullandığınla aynı yap

type AuthState = {
  token: string | null;
  isAuthenticated: boolean;
  hydrated: boolean;
  setToken: (token: string) => void;
  clearToken: () => void;
  hydrate: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  isAuthenticated: false,
  hydrated: false,

  setToken: (token) => {
    localStorage.setItem(STORAGE_KEY, token);
    set({ token, isAuthenticated: true, hydrated: true });
  },

  clearToken: () => {
    localStorage.removeItem(STORAGE_KEY);
    set({ token: null, isAuthenticated: false, hydrated: true });
  },

  hydrate: () => {
    const token = localStorage.getItem(STORAGE_KEY);
    set({
      token: token || null,
      isAuthenticated: !!token,
      hydrated: true,
    });
  },
}));
