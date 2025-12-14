import { useState } from "react";
import { Link } from "react-router-dom";
import Drawer from "./Drawer";
import SideMenu from "./SideMenu";

export default function Navbar() {
  const [open, setOpen] = useState(false);

  function close() {
    setOpen(false);
  }

  return (
    <>
      <header className="border-b border-slate-900 bg-slate-950/60 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={() => setOpen(true)}
              className="rounded-lg border border-slate-800 px-3 py-2 text-sm text-slate-200 hover:bg-slate-900"
              aria-label="Open menu"
            >
              ☰
            </button>

            <Link to="/main" className="font-semibold tracking-tight">
              Investment Tracker
            </Link>
          </div>

          {/* Sağ taraf: şimdilik boş, ileride user/avatar/notification vs. koyarız */}
          <div className="text-xs text-slate-400">Frontend only</div>
        </div>
      </header>

      <Drawer open={open} onClose={close} title="Navigation">
        <SideMenu onNavigate={close} />

        <div className="mt-4 rounded-xl border border-slate-900 bg-slate-950/40 p-3">
          <div className="text-xs font-medium uppercase tracking-wide text-slate-400">
            Notes
          </div>
          <p className="mt-2 text-sm text-slate-300">
            Backend entegre edilince buraya kullanıcı bilgisi ve logout ekleyeceğiz.
          </p>
        </div>
      </Drawer>
    </>
  );
}
