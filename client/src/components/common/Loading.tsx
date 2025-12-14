export default function Loading({ label = "Loading..." }: { label?: string }) {
  return (
    <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-4 text-sm text-slate-300">
      {label}
    </div>
  );
}
