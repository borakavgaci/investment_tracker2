import PageHeader from "../../components/common/PageHeader";

export default function PortfolioPage() {
  return (
    <div>
      <PageHeader title="Portfolio" subtitle="Holdings and performance summary (frontend only)." />

      <div className="grid gap-4 md:grid-cols-2">
        <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
          <div className="text-sm text-slate-300">Total Value</div>
          <div className="mt-2 text-2xl font-semibold">₺0.00</div>
          <div className="mt-1 text-xs text-slate-400">Calculated after API integration</div>
        </div>

        <div className="rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
          <div className="text-sm text-slate-300">Unrealized P/L</div>
          <div className="mt-2 text-2xl font-semibold">₺0.00</div>
          <div className="mt-1 text-xs text-slate-400">Placeholder</div>
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-slate-900 bg-slate-950/40 p-4">
        <div className="mb-3 text-sm font-medium text-slate-200">Holdings</div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-slate-400">
              <tr>
                <th className="py-2">Symbol</th>
                <th className="py-2">Quantity</th>
                <th className="py-2">Avg. Cost</th>
                <th className="py-2">Market Price</th>
              </tr>
            </thead>
            <tbody className="text-slate-200">
              {["AAPL", "THYAO"].map((s) => (
                <tr key={s} className="border-t border-slate-900">
                  <td className="py-2 font-medium">{s}</td>
                  <td className="py-2">0</td>
                  <td className="py-2">0.00</td>
                  <td className="py-2">0.00</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <button className="mt-4 rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-slate-200 hover:bg-slate-900">
          Add Trade (later)
        </button>
      </div>
    </div>
  );
}
