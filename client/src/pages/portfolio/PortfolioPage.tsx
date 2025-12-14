import React, { useEffect, useState } from "react";
import { getMyPortfolio } from "../../features/portfolio/portfolio.api";
import type { PortfolioHoldingDto } from "../../features/portfolio/portfolio.api";

import { Link } from "react-router-dom";

function money(v: number, currency = "USD") {
  return new Intl.NumberFormat("en-US", { style: "currency", currency }).format(v);
}

export default function PortfolioPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [balance, setBalance] = useState<number>(0);
  const [rows, setRows] = useState<PortfolioHoldingDto[]>([]);

  async function load() {
    setLoading(true);
    setError("");
    try {
      const p = await getMyPortfolio();
      setBalance(Number(p.balance ?? 0));
      setRows(p.holdings ?? []);
    } catch (e: any) {
      setError(e?.response?.data?.message || e?.message || "Portfolio load failed");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="space-y-4">
      <div className="rounded-2xl border border-slate-800 bg-slate-950 p-5">
        <div className="flex items-center justify-between">
          <div>
            <div className="text-sm text-slate-400">Wallet Balance</div>
            <div className="text-xl font-semibold text-white">{money(balance)}</div>
          </div>
          <button
            onClick={load}
            disabled={loading}
            className="rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-white"
          >
            {loading ? "Loading..." : "Refresh"}
          </button>
        </div>

        {error && <div className="mt-3 text-sm text-red-400">{error}</div>}
      </div>

      <div className="rounded-2xl border border-slate-800 bg-slate-950 p-5">
        <div className="mb-3 text-sm font-medium text-white">Holdings ({rows.length})</div>

        {rows.length === 0 && !loading && !error && (
          <div className="text-sm text-slate-400">No holdings.</div>
        )}

        <div className="space-y-2">
          {rows.map((h) => (
            <div
              key={h.stockId}
              className="flex items-center justify-between rounded-xl border border-slate-800 px-3 py-2"
            >
              <div className="min-w-0">
                <Link
                  to={`/stocks/${encodeURIComponent(h.symbol)}`}
                  className="text-sm font-medium text-white underline"
                >
                  {h.symbol}
                </Link>
                <div className="truncate text-xs text-slate-400">{h.companyName}</div>
              </div>

              <div className="text-right">
                <div className="text-sm text-white">
                  Qty {Number(h.quantity).toFixed(4)} · MV {money(Number(h.marketValue))}
                </div>
                <div className="text-xs text-slate-500">
                  P/L {money(Number(h.unrealizedPnL))} ({Number(h.unrealizedPnLPct).toFixed(2)}%)
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
