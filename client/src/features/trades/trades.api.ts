import { http } from "../../services/http";

export type TradeType = "BUY" | "SELL";

export interface TradeRequestDto {
  symbol: string;
  type: TradeType;
  quantity: number; // backend BigDecimal
}

export interface TradeResponseDto {
  tradeId: string;
  symbol: string;
  type: TradeType;
  quantity: number;
  price: number;
  costOrProceeds: number;
  walletBalanceAfter: number;
  tradeTime: string;
}

export async function createTrade(payload: TradeRequestDto) {
  const res = await http.post<TradeResponseDto>("/api/trades", payload);
  return res.data;
}
