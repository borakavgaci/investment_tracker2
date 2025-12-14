import axios from "axios";
import { STORAGE_KEY } from "../features/auth/auth.store";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: false,
});

function readToken(): string | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;

  const t = raw.trim();
  if ((t.startsWith('"') && t.endsWith('"')) || (t.startsWith("'") && t.endsWith("'"))) {
    return t.slice(1, -1);
  }
  return t;
}

api.interceptors.request.use((config) => {
  const token = readToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err?.response?.status === 401) {
      const path = window.location.pathname || "";
      if (!path.startsWith("/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default api;
