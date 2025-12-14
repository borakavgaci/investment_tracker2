import { NavLink } from "react-router-dom";

function linkClass({ isActive }: { isActive: boolean }) {
  return [
    "block rounded-lg px-3 py-2 text-sm transition",
    isActive ? "bg-slate-800 text-white" : "text-slate-300 hover:bg-slate-900 hover:text-white",
  ].join(" ");
}

export default function Sidebar() {
  return (
    <aside className="hidden w-64 shrink-0 md:block">
      <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-3">
        <div className="px-3 py-2 text-xs font-medium uppercase tracking-wide text-slate-400">
          Navigation
        </div>
        <nav className="mt-2 space-y-1">
          <NavLink to="/main" className={linkClass}>
            Main
          </NavLink>
          <NavLink to="/portfolio" className={linkClass}>
            Portfolio
          </NavLink>
          <NavLink to="/wallet" className={linkClass}>
            Wallet
          </NavLink>
        </nav>
      </div>
    </aside>
  );
}
