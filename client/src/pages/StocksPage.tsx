import React, { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";

const API_BASE = "http://localhost:8080";

type SortKey = "symbol" | "companyName" | "currentValue" | "lastPriceUpdatedAt";
type SortDir = "asc" | "desc";

interface StockResponseDto {
  id: string;
  symbol: string;
  companyName: string;
  country: string;
  currentValue: number;
  lastPriceUpdatedAt: string | null;
  createdAt: string;
}

function formatPrice(value: unknown) {
  if (value === null || value === undefined) return "-";
  const n = typeof value === "number" ? value : Number(value);
  if (Number.isNaN(n)) return String(value);
  return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(n);
}

function formatTime(iso: string | null | undefined) {
  if (!iso) return "-";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString();
}

export default function StocksPage() {
  const [country, setCountry] = useState<string>("US");
  const [limit, setLimit] = useState<number>(50);

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const [rows, setRows] = useState<StockResponseDto[]>([]);

  const [q, setQ] = useState<string>("");
  const [sortKey, setSortKey] = useState<SortKey>("symbol");
  const [sortDir, setSortDir] = useState<SortDir>("asc");

  async function fetchStocks() {
    setLoading(true);
    setError("");

    try {
      const token = localStorage.getItem("token");
      const url = new URL(`${API_BASE}/api/stocks`);
      url.searchParams.set("country", country);
      url.searchParams.set("limit", String(limit));

      const res = await fetch(url.toString(), {
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
      });

      if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`HTTP ${res.status}${text ? ` - ${text}` : ""}`);
      }

      const data = (await res.json()) as unknown;

      if (!Array.isArray(data)) {
        setRows([]);
        return;
      }

      const normalized: StockResponseDto[] = data.map((r: any) => ({
        id: String(r.id ?? ""),
        symbol: String(r.symbol ?? ""),
        companyName: String(r.companyName ?? ""),
        country: String(r.country ?? ""),
        currentValue: typeof r.currentValue === "number" ? r.currentValue : Number(r.currentValue ?? 0),
        lastPriceUpdatedAt: r.lastPriceUpdatedAt ? String(r.lastPriceUpdatedAt) : null,
        createdAt: String(r.createdAt ?? ""),
      }));

      setRows(normalized);
    } catch (e: any) {
      setError(e?.message || "Fetch failed");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchStocks();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const filtered = useMemo(() => {
    const query = q.trim().toLowerCase();
    if (!query) return rows;

    return rows.filter((r) => {
      const sym = (r.symbol || "").toLowerCase();
      const name = (r.companyName || "").toLowerCase();
      return sym.includes(query) || name.includes(query);
    });
  }, [rows, q]);

  const sorted = useMemo(() => {
    const arr = [...filtered];
    arr.sort((a, b) => {
      const dir = sortDir === "asc" ? 1 : -1;

      const av = a[sortKey];
      const bv = b[sortKey];

      if (sortKey === "currentValue") {
        const an = Number(av ?? -Infinity);
        const bn = Number(bv ?? -Infinity);
        return (an - bn) * dir;
      }

      if (sortKey === "lastPriceUpdatedAt") {
        const at = av ? new Date(String(av)).getTime() : -Infinity;
        const bt = bv ? new Date(String(bv)).getTime() : -Infinity;
        return (at - bt) * dir;
      }

      const as = String(av ?? "").toLowerCase();
      const bs = String(bv ?? "").toLowerCase();
      return as.localeCompare(bs) * dir;
    });
    return arr;
  }, [filtered, sortKey, sortDir]);

  function toggleSort(key: SortKey) {
    if (sortKey !== key) {
      setSortKey(key);
      setSortDir("asc");
      return;
    }
    setSortDir((d) => (d === "asc" ? "desc" : "asc"));
  }

  return (
    <div style={{ padding: 16, maxWidth: 1100, margin: "0 auto" }}>
      <h2>Stocks</h2>

      <div style={{ display: "flex", gap: 12, flexWrap: "wrap", alignItems: "center", marginBottom: 12 }}>
        <label>
          Country&nbsp;
          <select value={country} onChange={(e) => setCountry(e.target.value)}>
            <option value="US">US</option>
            <option value="TR">TR</option>
          </select>
        </label>

        <label>
          Limit&nbsp;
          <input
            type="number"
            min={1}
            max={50}
            value={limit}
            onChange={(e) => setLimit(Number(e.target.value))}
            style={{ width: 80 }}
          />
        </label>

        <button
          onClick={fetchStocks}
          disabled={loading}
          style={{ padding: "8px 12px", cursor: loading ? "not-allowed" : "pointer" }}
        >
          {loading ? "Loading..." : "Refresh"}
        </button>

        <input
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="Search symbol or company..."
          style={{ flex: 1, minWidth: 220, padding: 8 }}
        />
      </div>

      {error && (
        <div style={{ padding: 12, border: "1px solid #c00", color: "#c00", marginBottom: 12 }}>
          {error}
        </div>
      )}

      <div style={{ overflowX: "auto", border: "1px solid #ddd", borderRadius: 8 }}>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ background: "#f7f7f7" }}>
              <th style={th} onClick={() => toggleSort("symbol")}>Symbol</th>
              <th style={th} onClick={() => toggleSort("companyName")}>Company</th>
              <th style={th} onClick={() => toggleSort("currentValue")}>Price</th>
              <th style={th} onClick={() => toggleSort("lastPriceUpdatedAt")}>Last Updated</th>
              <th style={th}>Country</th>
            </tr>
          </thead>

          <tbody>
            {sorted.map((r) => (
              <tr key={r.id} style={{ borderTop: "1px solid #eee" }}>
                <td style={tdMono}>
                  <Link to={`/stocks/${encodeURIComponent(r.symbol)}`} style={{ textDecoration: "none" }}>
                    {r.symbol}
                  </Link>
                </td>
                <td style={td}>{r.companyName}</td>
                <td style={td}>{formatPrice(r.currentValue)}</td>
                <td style={td}>{formatTime(r.lastPriceUpdatedAt)}</td>
                <td style={td}>{r.country}</td>
              </tr>
            ))}

            {!loading && sorted.length === 0 && (
              <tr>
                <td style={td} colSpan={5}>
                  No data
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

const th: React.CSSProperties = {
  textAlign: "left",
  padding: "10px 12px",
  cursor: "pointer",
  userSelect: "none",
  whiteSpace: "nowrap",
};

const td: React.CSSProperties = {
  padding: "10px 12px",
  verticalAlign: "top",
};

const tdMono: React.CSSProperties = {
  ...td,
  fontFamily:
    "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace",
};
