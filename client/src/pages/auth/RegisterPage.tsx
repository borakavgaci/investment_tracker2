import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../../features/auth/auth.api";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    surname: "",
    email: "",
    password: "",
  });

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
      // Normalize (backend tarafında lower-case yapıyorsun ama burada da temiz olsun)
      const payload = {
        ...form,
        email: form.email.trim().toLowerCase(),
      };

      await registerUser(payload);
      navigate("/auth/login"); // kayıt başarılı → login
    } catch (err: any) {
      const status = err?.response?.status;
      const data = err?.response?.data;

      // Backend'ten gelen message/code varsa onu öncelikle göster
      if (status === 409) {
        setError("Bu e-posta zaten kayıtlı.");
      } else if (status === 400) {
        setError(data?.message || "Geçersiz kayıt bilgisi.");
      } else {
        setError(data?.message || "Kayıt sırasında hata oluştu.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <input
          name="name"
          placeholder="Name"
          value={form.name}
          onChange={onChange}
          className="input"
        />
        <input
          name="surname"
          placeholder="Surname"
          value={form.surname}
          onChange={onChange}
          className="input"
        />
      </div>

      <input
        name="email"
        type="email"
        placeholder="Email"
        value={form.email}
        onChange={onChange}
        className="input"
      />

      <input
        name="password"
        type="password"
        placeholder="Password"
        value={form.password}
        onChange={onChange}
        className="input"
      />

      {error && <div className="text-sm text-red-400">{error}</div>}

      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-xl bg-white px-3 py-2 text-sm font-medium text-slate-900 hover:bg-slate-200 disabled:opacity-60"
      >
        {loading ? "Creating..." : "Create account"}
      </button>

      <div className="text-sm text-slate-300">
        Already have an account?{" "}
        <Link to="/auth/login" className="underline">
          Login
        </Link>
      </div>
    </form>
  );
}
