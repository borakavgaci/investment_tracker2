import { Outlet } from "react-router-dom";
import Navbar from "../../components/common/Navbar";

export function MainLayout() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <Navbar />

      <div className="mx-auto max-w-7xl px-4 py-6">
        <main className="min-w-0">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
