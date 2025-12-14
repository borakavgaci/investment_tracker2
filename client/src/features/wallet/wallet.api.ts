import { http } from "../../services/http";
import type { WalletResponse, WalletTxResponse } from "./wallet.types";

export async function depositFixed(): Promise<WalletResponse> {
  const res = await http.post<WalletResponse>("/api/wallet/deposit");
  return res.data;
}

export async function withdrawFixed(): Promise<WalletResponse> {
  const res = await http.post<WalletResponse>("/api/wallet/withdraw");
  return res.data;
}

export async function getMyWallet(): Promise<WalletResponse> {
  const res = await http.get<WalletResponse>("/api/wallet/me");
  return res.data;
}

export async function getRecentTransactions(): Promise<WalletTxResponse[]> {
  const res = await http.get<WalletTxResponse[]>("/api/wallet/transactions");
  return res.data;
}
