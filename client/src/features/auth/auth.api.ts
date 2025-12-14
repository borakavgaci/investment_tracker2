import { http } from "../../services/http";

export type RegisterPayload = {
  name: string;
  surname: string;
  email: string;
  password: string;
};

export async function registerUser(payload: RegisterPayload) {
  const res = await http.post("/api/auth/register", payload);
  return res.data;
}

export type LoginPayload = {
  email: string;
  password: string;
};

export async function loginUser(payload: LoginPayload) {
  const res = await http.post("/api/auth/login", payload);
  return res.data as { token: string; tokenType: string };
}

