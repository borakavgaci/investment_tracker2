export type WalletResponse = {
  walletId: string;
  freeBalance: string; // BigDecimal -> string gelir
  lastUpdated: string;
};

export type WalletTxResponse = {
  id: string;
  walletId: string;
  type: "deposit" | "withdrawal" | string;
  amount: string;
  transactionDate: string;
};
