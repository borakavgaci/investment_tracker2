import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../../features/auth/auth.store";

export function ProtectedRoute() {
  const hydrated = useAuthStore((s) => s.hydrated);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  if (!hydrated) {
    // Hydrate bitene kadar boş ekran / istersen loader
    return (
      <div className="min-h-[40vh] grid place-items-center text-slate-400">
        Loading...
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/auth/login" replace />;
  }

  return <Outlet />;
}
