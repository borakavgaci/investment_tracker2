import React, { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/client";
import { createTrade } from "../features/trades/trades.api";
import { getMyPortfolio } from "../features/portfolio/portfolio.api";

interface StockDetailResponseDto {
  id: string;
  symbol: string;
  companyName: string;
  country: string;

  currentValue: number | string;
  open: number | string | null;
  high: number | string | null;
  low: number | string | null;
  prevClose: number | string | null;
  change: number | string | null;
  changePercent: number | string | null;
  quoteTime: string | null;

  exchange: string | null;
  industry: string | null;
  website: string | null;
  logo: string | null;
  currency: string | null;

  lastPriceUpdatedAt: string | null;
  createdAt: string;
}

function toNum(v: unknown): number | null {
  if (v === null || v === undefined) return null;
  const n = typeof v === "number" ? v : Number(v);
  return Number.isNaN(n) ? null : n;
}

function money(v: unknown, currency?: string | null) {
  const n = toNum(v);
  if (n === null) return "-";
  const cur = currency || "USD";
  return new Intl.NumberFormat("en-US", { style: "currency", currency: cur }).format(n);
}

function time(iso: string | null | undefined) {
  if (!iso) return "-";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString();
}

export default function StockDetailPage() {
  const { symbol } = useParams<{ symbol: string }>();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [data, setData] = useState<StockDetailResponseDto | null>(null);

  const [portfolioLoading, setPortfolioLoading] = useState(false);
  const [balance, setBalance] = useState<number | null>(null);
  const [ownedQty, setOwnedQty] = useState<number>(0);
  const [avgCost, setAvgCost] = useState<number>(0);

  const [qty, setQty] = useState<string>("1");

  async function fetchDetail() {
    if (!symbol) return;
    setLoading(true);
    setError("");

    try {
      const res = await api.get(`/api/stocks/${encodeURIComponent(symbol)}`);
      setData(res.data as StockDetailResponseDto);
    } catch (e: any) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        (typeof e?.response?.data === "string" ? e.response.data : "") ||
        e?.message ||
        "Fetch failed";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }

  async function fetchPortfolio() {
    if (!symbol) return;
    setPortfolioLoading(true);

    try {
      const p = await getMyPortfolio();
      setBalance(Number(p.balance ?? 0));

      const h = (p.holdings || []).find((x: any) => x.symbol?.toUpperCase() === symbol.toUpperCase());
      if (h) {
        setOwnedQty(Number(h.quantity ?? 0));
        setAvgCost(Number(h.avgCost ?? 0));
      } else {
        setOwnedQty(0);
        setAvgCost(0);
      }
    } catch {
      // ignore
    } finally {
      setPortfolioLoading(false);
    }
  }

  useEffect(() => {
    fetchDetail();
    fetchPortfolio();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [symbol]);

  const currentPrice = useMemo(() => toNum(data?.currentValue) ?? 0, [data]);

  const qtyNum = useMemo(() => {
    const n = Number(qty);
    if (!Number.isFinite(n) || n <= 0) return 0;
    return n;
  }, [qty]);

  const buyCostPreview = useMemo(() => qtyNum * currentPrice, [qtyNum, currentPrice]);

  const pnlPreview = useMemo(() => {
    if (!ownedQty || !currentPrice) return null;
    return (currentPrice - avgCost) * ownedQty;
  }, [ownedQty, avgCost, currentPrice]);

  const canSell = useMemo(() => {
    if (ownedQty <= 0) return false;
    if (qtyNum <= 0) return false;
    return qtyNum <= ownedQty;
  }, [ownedQty, qtyNum]);

  async function onBuy() {
    if (!symbol) return;
    setError("");
    if (qtyNum <= 0) return setError("Quantity must be > 0");

    try {
      await createTrade({ symbol, type: "BUY", quantity: qtyNum });
      await fetchPortfolio();
    } catch (e: any) {
      setError(e?.response?.data?.message || e?.response?.data?.error || e?.message || "BUY failed");
    }
  }

  async function onSell() {
    if (!symbol) return;
    setError("");
    if (qtyNum <= 0) return setError("Quantity must be > 0");
    if (qtyNum > ownedQty) return setError("Quantity exceeds owned amount");

    try {
      await createTrade({ symbol, type: "SELL", quantity: qtyNum });
      await fetchPortfolio();
    } catch (e: any) {
      setError(e?.response?.data?.message || e?.response?.data?.error || e?.message || "SELL failed");
    }
  }

  return (
    <div style={{ padding: 16, maxWidth: 1100, margin: "0 auto" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 12 }}>
        <div>
          <h2 style={{ margin: 0 }}>{symbol}</h2>
          <div style={{ opacity: 0.8 }}>{data?.companyName || ""}</div>
        </div>

        <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
          <Link to="/main" style={{ textDecoration: "none" }}>← Back</Link>
          <button
            onClick={() => { fetchDetail(); fetchPortfolio(); }}
            disabled={loading || portfolioLoading}
            style={{ padding: "8px 12px" }}
          >
            {(loading || portfolioLoading) ? "Loading..." : "Refresh"}
          </button>
        </div>
      </div>

      {error && (
        <div style={{ marginTop: 12, padding: 12, border: "1px solid #c00", color: "#c00" }}>
          {error}
        </div>
      )}

      <div style={{ marginTop: 14, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
        <div style={card}>
          <div style={cardTitle}>Trade</div>

          <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap" }}>
            <label>
              Quantity&nbsp;
              <input
                value={qty}
                onChange={(e) => setQty(e.target.value)}
                type="number"
                min={0}
                step="0.0001"
                style={{ padding: 8, width: 140 }}
              />
            </label>

            <button onClick={onBuy} style={{ padding: "8px 12px" }}>
              Buy
            </button>

            <button onClick={onSell} disabled={!canSell} style={{ padding: "8px 12px" }}>
              Sell
            </button>
          </div>

          <div style={{ marginTop: 10, opacity: 0.85 }}>
            Current price: <b>{money(currentPrice, data?.currency)}</b>
          </div>

          <div style={{ marginTop: 8, opacity: 0.85 }}>
            Buy cost (preview): <b>{money(buyCostPreview, data?.currency)}</b>
          </div>

          <hr style={{ margin: "12px 0", borderColor: "#222" }} />

          <div style={{ opacity: 0.85 }}>
            Wallet balance: <b>{balance === null ? "-" : money(balance, data?.currency)}</b>
          </div>
          <div style={{ marginTop: 6, opacity: 0.85 }}>
            You own: <b>{ownedQty.toFixed(4)}</b> @ avg <b>{money(avgCost, data?.currency)}</b>
          </div>
          <div style={{ marginTop: 6, opacity: 0.85 }}>
            Unrealized P/L: <b>{pnlPreview === null ? "-" : money(pnlPreview, data?.currency)}</b>
          </div>
        </div>

        <div style={card}>
          <div style={cardTitle}>Quote</div>
          <Row k="Price" v={<b>{money(data?.currentValue, data?.currency)}</b>} />
          <Row k="Last DB Price Update" v={time(data?.lastPriceUpdatedAt)} />
          <Row k="Quote time" v={time(data?.quoteTime)} />
          <Row k="Country" v={data?.country || "-"} />
        </div>
      </div>

      {data && (
        <div style={{ marginTop: 12, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
          <div style={card}>
            <div style={cardTitle}>Company</div>
            <Row k="Exchange" v={data.exchange || "-"} />
            <Row k="Industry" v={data.industry || "-"} />
            <Row
              k="Website"
              v={
                data.website ? (
                  <a href={data.website} target="_blank" rel="noreferrer">{data.website}</a>
                ) : (
                  "-"
                )
              }
            />
            {data.logo && (
              <div style={{ marginTop: 10 }}>
                <img src={data.logo} alt="logo" style={{ maxHeight: 48, maxWidth: "100%" }} />
              </div>
            )}
          </div>

          <div style={card}>
            <div style={cardTitle}>Meta</div>
            <Row k="Created At" v={time(data.createdAt)} />
          </div>
        </div>
      )}
    </div>
  );
}

function Row({ k, v }: { k: string; v: React.ReactNode }) {
  return (
    <div style={{ display: "flex", justifyContent: "space-between", gap: 12, marginTop: 8 }}>
      <div style={{ opacity: 0.8 }}>{k}</div>
      <div style={{ textAlign: "right" }}>{v}</div>
    </div>
  );
}

const card: React.CSSProperties = {
  border: "1px solid #222",
  borderRadius: 12,
  padding: 14,
};

const cardTitle: React.CSSProperties = {
  fontWeight: 700,
  marginBottom: 8,
};
