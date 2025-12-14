import { NavLink, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../features/auth/auth.store";

function linkClass({ isActive }: { isActive: boolean }) {
  return [
    "block rounded-xl px-3 py-2 text-sm transition",
    isActive
      ? "bg-slate-800 text-white"
      : "text-slate-300 hover:bg-slate-900 hover:text-white",
  ].join(" ");
}

export default function SideMenu({ onNavigate }: { onNavigate?: () => void }) {
  const navigate = useNavigate();
  const clearToken = useAuthStore((s) => s.clearToken);

  function handleLogout() {
    clearToken();                       // token + auth state silinir
    navigate("/auth/login", { replace: true });
    onNavigate?.();                     // hamburger menüyü kapat
  }

  return (
    <nav className="space-y-1">
      <NavLink to="/main" className={linkClass} onClick={onNavigate}>
        Main
      </NavLink>

      <NavLink to="/portfolio" className={linkClass} onClick={onNavigate}>
        Portfolio
      </NavLink>

      <NavLink to="/wallet" className={linkClass} onClick={onNavigate}>
        Wallet
      </NavLink>

      {/* Logout */}
      <button
        type="button"
        onClick={handleLogout}
        className="mt-4 block w-full rounded-xl px-3 py-2 text-left text-sm text-red-400 transition hover:bg-red-500/10 hover:text-red-300"
      >
        Logout
      </button>
    </nav>
  );
}
