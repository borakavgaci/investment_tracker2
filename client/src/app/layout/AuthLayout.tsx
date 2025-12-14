import { Link, Outlet } from "react-router-dom";

export function AuthLayout() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="mx-auto flex min-h-screen max-w-6xl items-center justify-center px-4 py-10">
        <div className="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-900/40 p-6 shadow">
          <div className="mb-6">
            <Link to="/main" className="text-sm text-slate-300 hover:text-white">
              Investment Tracker
            </Link>
            <h1 className="mt-2 text-2xl font-semibold">Welcome</h1>
            <p className="mt-1 text-sm text-slate-300">
              Sign in or create an account to continue.
            </p>
          </div>

          <Outlet />

          <p className="mt-6 text-xs text-slate-400">
            Course project UI. Backend integration will be added later.
          </p>
        </div>
      </div>
    </div>
  );
}
