import PageHeader from "../../components/common/PageHeader";

export default function MainPage() {
  return (
    <div>
      <PageHeader
        title="Main"
        subtitle="Market overview and quick actions (frontend only for now)."
        right={
          <button className="rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-slate-200 hover:bg-slate-900">
            Refresh
          </button>
        }
      />

      <div className="grid gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
          <div className="text-sm text-slate-300">Selected Market</div>
          <div className="mt-2 text-lg font-semibold">BIST / NASDAQ</div>
          <div className="mt-1 text-xs text-slate-400">Placeholder</div>
        </div>

        <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
          <div className="text-sm text-slate-300">Watchlist</div>
          <div className="mt-2 text-lg font-semibold">0 stocks</div>
          <div className="mt-1 text-xs text-slate-400">Will be filled via API</div>
        </div>

        <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
          <div className="text-sm text-slate-300">Today P/L</div>
          <div className="mt-2 text-lg font-semibold">₺0.00</div>
          <div className="mt-1 text-xs text-slate-400">Frontend placeholder</div>
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
        <div className="mb-3 text-sm font-medium text-slate-200">Stocks (placeholder)</div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-slate-400">
              <tr>
                <th className="py-2">Symbol</th>
                <th className="py-2">Company</th>
                <th className="py-2">Price</th>
                <th className="py-2">Change</th>
              </tr>
            </thead>
            <tbody className="text-slate-200">
              {["AAPL", "TSLA", "THYAO"].map((s) => (
                <tr key={s} className="border-t border-slate-900">
                  <td className="py-2 font-medium">{s}</td>
                  <td className="py-2 text-slate-300">Placeholder Inc.</td>
                  <td className="py-2">0.00</td>
                  <td className="py-2 text-slate-300">0.00%</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
