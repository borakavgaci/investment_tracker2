import { http } from "../../services/http";

export interface PortfolioHoldingDto {
  stockId: string;
  symbol: string;
  companyName: string;
  quantity: number;
  avgCost: number;
  currentPrice: number;
  marketValue: number;
  unrealizedPnL: number;
  unrealizedPnLPct: number;
}

export interface PortfolioResponseDto {
  walletId: string;
  balance: number;
  holdings: PortfolioHoldingDto[];
}

export async function getMyPortfolio() {
  const res = await http.get<PortfolioResponseDto>("/api/portfolio/me");
  return res.data;
}
