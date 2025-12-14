import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../../features/auth/auth.api";
import { useAuthStore } from "../../features/auth/auth.store";

export default function LoginPage() {
  const navigate = useNavigate();
  const setToken = useAuthStore((s) => s.setToken);

  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function onChange(e: React.ChangeEvent<HTMLInputElement>) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const payload = {
        email: form.email.trim().toLowerCase(),
        password: form.password,
      };

      const data = await loginUser(payload);

      // Bazı backend'ler "token" yerine farklı alan döndürebilir.
      // Biz güvenli şekilde ikisini de destekleyelim:
      const token = data.token;


      if (!token) {
        setError("Giriş başarılı görünüyor ama token alınamadı.");
        return;
      }

      setToken(token); // localStorage + isAuthenticated=true
      navigate("/main");
    } catch (err: any) {
      const status = err?.response?.status;
      const data = err?.response?.data;

      if (status === 401) {
        setError("Email veya şifre hatalı.");
      } else {
        setError(data?.message || "Giriş sırasında hata oluştu.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <div>
        <label className="mb-1 block text-sm text-slate-200">Email</label>
        <input
          name="email"
          type="email"
          value={form.email}
          onChange={onChange}
          className="w-full rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm outline-none focus:border-slate-600"
          placeholder="bora@example.com"
        />
      </div>

      <div>
        <label className="mb-1 block text-sm text-slate-200">Password</label>
        <input
          name="password"
          type="password"
          value={form.password}
          onChange={onChange}
          className="w-full rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm outline-none focus:border-slate-600"
          placeholder="12345678"
        />
      </div>

      {error && <div className="text-sm text-red-400">{error}</div>}

      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-xl bg-white px-3 py-2 text-sm font-medium text-slate-900 hover:bg-slate-200 disabled:opacity-60"
      >
        {loading ? "Signing in..." : "Sign in"}
      </button>

      <div className="flex items-center justify-between text-sm">
        <span className="text-slate-300">No account?</span>
        <Link
          to="/auth/register"
          className="text-white underline decoration-slate-500 hover:decoration-white"
        >
          Create one
        </Link>
      </div>
    </form>
  );
}
