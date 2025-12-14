import type { ReactNode } from "react";

export default function Drawer({
  open,
  onClose,
  title = "Menu",
  children,
}: {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
}) {
  return (
    <div
      className={[
        "fixed inset-0 z-50",
        open ? "pointer-events-auto" : "pointer-events-none",
      ].join(" ")}
      aria-hidden={!open}
    >
      {/* Backdrop */}
      <div
        onClick={onClose}
        className={[
          "absolute inset-0 bg-black/60 transition-opacity",
          open ? "opacity-100" : "opacity-0",
        ].join(" ")}
      />

      {/* Panel */}
      <div
        className={[
          "absolute left-0 top-0 h-full w-72 max-w-[85vw]",
          "border-r border-slate-900 bg-slate-950 text-slate-100",
          "transition-transform duration-200 ease-out",
          open ? "translate-x-0" : "-translate-x-full",
        ].join(" ")}
        role="dialog"
        aria-modal="true"
        aria-label={title}
      >
        <div className="flex items-center justify-between border-b border-slate-900 px-4 py-3">
          <div className="text-sm font-semibold">{title}</div>
          <button
            onClick={onClose}
            className="rounded-lg border border-slate-800 px-2 py-1 text-xs text-slate-200 hover:bg-slate-900"
          >
            Close
          </button>
        </div>

        <div className="p-3">{children}</div>
      </div>
    </div>
  );
}
