import { useEffect, useState } from "react";
import { getStocksByCountry } from "../../features/stocks/stocks.api";
import type { Stock } from "../../features/stocks/stocks.types";
import { Link } from "react-router-dom";

const COUNTRIES = [
  { code: "US", name: "United States" },
  { code: "DE", name: "Germany" },
  { code: "GB", name: "United Kingdom" },
  { code: "CA", name: "Canada" },
];

export default function MainPage() {
  const [country, setCountry] = useState("US");
  const [stocks, setStocks] = useState<Stock[]>([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchStocks(c: string) {
    setLoading(true);
    setError(null);
    try {
      const data = await getStocksByCountry(c, 50);
      setStocks(data);
    } catch (err: any) {
      const status = err?.response?.status;
      const data = err?.response?.data;

      if (status === 400) {
        setStocks([]);
        setError("Bu ülke için veri yok (upstream izin vermiyor veya DB boş).");
      } else if (status === 401) {
        setError("Yetkisiz. Lütfen tekrar giriş yap.");
      } else {
        setError(data?.message || "Stoklar alınırken hata oluştu.");
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchStocks(country);
  }, [country]);

  return (
    <div className="space-y-6">
      <div className="rounded-2xl border border-slate-800 bg-slate-950 p-5">
        <div className="text-sm text-slate-400">Select Country</div>

        <div className="mt-3 flex flex-wrap items-center gap-3">
          <select
            value={country}
            onChange={(e) => setCountry(e.target.value)}
            className="rounded-xl border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-white outline-none focus:border-slate-600"
          >
            {COUNTRIES.map((c) => (
              <option key={c.code} value={c.code}>
                {c.name} ({c.code})
              </option>
            ))}
          </select>

          {loading && <span className="text-sm text-slate-400">Loading...</span>}
        </div>

        {error && <div className="mt-3 text-sm text-red-400">{error}</div>}
      </div>

      <div className="rounded-2xl border border-slate-800 bg-slate-950 p-5">
        <div className="mb-3 text-sm font-medium text-white">
          Stocks ({stocks.length})
        </div>

        {!loading && stocks.length === 0 && !error && (
          <div className="text-sm text-slate-400">No stocks.</div>
        )}

        <div className="space-y-2">
          {stocks.map((s) => (
            <Link
              key={s.id}
              to={`/stocks/${encodeURIComponent(s.symbol)}`}
              className="block"
            >
              <div className="flex items-center justify-between rounded-xl border border-slate-800 px-3 py-2 transition-colors hover:border-slate-600 hover:bg-slate-900/40 cursor-pointer">
                <div className="min-w-0">
                  <div className="text-sm font-medium text-white">
                    {s.symbol}
                  </div>
                  <div className="truncate text-xs text-slate-400">
                    {s.companyName}
                  </div>
                </div>

                <div className="text-right">
                  <div className="text-sm text-white">{s.currentValue}</div>
                  <div className="text-xs text-slate-500">{s.country}</div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}
