import { http } from "../../services/http";

export type WalletDto = {
  id: string;
  userId: string;
  balance: number | string;
};

export async function getMyWallet(): Promise<WalletDto> {
  const res = await http.get<WalletDto>("/api/wallet/me");
  return res.data;
}

export async function deposit(amount: number): Promise<WalletDto> {
  const res = await http.post<WalletDto>("/api/wallet/deposit", { amount });
  return res.data;
}

export async function withdraw(amount: number): Promise<WalletDto> {
  const res = await http.post<WalletDto>("/api/wallet/withdraw", { amount });
  return res.data;
}
