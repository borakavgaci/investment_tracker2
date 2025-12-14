import { useEffect, useState } from "react";

import {
  getMyWallet,
  depositFixed,
  withdrawFixed,
  getRecentTransactions,
} from "../../features/wallet/wallet.api";

import type { WalletResponse, WalletTxResponse } from "../../features/wallet/wallet.types";

function formatTs(input: string) {
  const dt = new Date(input);
  if (Number.isNaN(dt.getTime())) return input; // fallback
  return dt.toLocaleString("tr-TR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
}

export default function WalletPage() {
  const [wallet, setWallet] = useState<WalletResponse | null>(null);
  const [txs, setTxs] = useState<WalletTxResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function refresh() {
    const [w, t] = await Promise.all([getMyWallet(), getRecentTransactions()]);
    setWallet(w);
    setTxs(t);
  }

  useEffect(() => {
    refresh();
  }, []);

  async function onDeposit() {
    setLoading(true);
    setError(null);
    try {
      await depositFixed();
      await refresh();
    } catch {
      setError("Deposit failed");
    } finally {
      setLoading(false);
    }
  }

  async function onWithdraw() {
    setLoading(true);
    setError(null);
    try {
      await withdrawFixed();
      await refresh();
    } catch {
      setError("Insufficient balance");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      {/* WALLET CARD */}
      <div className="rounded-2xl border border-slate-800 bg-slate-950 p-5">
        <div className="text-sm text-slate-400">Free Balance</div>
        <div className="mt-2 text-3xl font-semibold text-white">
          {wallet ? wallet.freeBalance : "—"}
        </div>

        {/* ✅ DEPOSIT / WITHDRAW */}
        <div className="mt-5 flex gap-3">
          <button
            onClick={onDeposit}
            disabled={loading}
            className="rounded-xl bg-emerald-500 px-4 py-2 text-sm font-medium text-black hover:bg-emerald-400 disabled:opacity-60"
          >
            Deposit +500
          </button>

          <button
            onClick={onWithdraw}
            disabled={loading}
            className="rounded-xl border border-slate-700 px-4 py-2 text-sm font-medium text-white hover:bg-slate-900 disabled:opacity-60"
          >
            Withdraw -500
          </button>
        </div>

        {error && <div className="mt-3 text-sm text-red-400">{error}</div>}
      </div>

      {/* TRANSACTIONS */}
      <div className="rounded-2xl border border-slate-800 bg-slate-950 p-5">
        <div className="mb-3 text-sm font-medium text-white">Recent Transactions</div>

        {txs.length === 0 && <div className="text-sm text-slate-400">No transactions</div>}

        <div className="space-y-2">
          {txs.map((t) => (
            <div
              key={t.id}
              className="flex items-center justify-between rounded-xl border border-slate-800 px-3 py-2"
            >
              <div className="flex flex-col">
                <span className="text-sm text-slate-300">{t.type}</span>
                <span className="text-xs text-slate-500">
                  {formatTs(t.transactionDate)}
                </span>
              </div>

              <span className="text-sm text-white">{t.amount}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
