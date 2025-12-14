import { http } from "../../services/http";
import type { Stock } from "./stocks.types";

export async function getStocksByCountry(
  country: string,
  limit = 50
): Promise<Stock[]> {
  const res = await http.get<Stock[]>("/api/stocks", {
    params: { country, limit },
  });
  return res.data;
}

// ✅ NEW: Stock detail
export type StockDetailResponseDto = {
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
};

export async function getStockDetail(symbol: string): Promise<StockDetailResponseDto> {
  const res = await http.get<StockDetailResponseDto>(`/api/stocks/${encodeURIComponent(symbol)}`);
  return res.data;
}
