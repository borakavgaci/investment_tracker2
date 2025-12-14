import { useEffect, useState } from "react";
import api from "../../api/client";

type WalletDto = {
  id: string;
  userId: string;
  balance: number | string;
};

function extractErrMsg(e: any) {
  const status = e?.response?.status;
  const data = e?.response?.data;
  if (status && data) {
    if (typeof data === "string") return `HTTP ${status} - ${data}`;
    if (typeof data?.message === "string") return data.message;
    if (typeof data?.error === "string") return data.error;
  }
  return e?.message || "ERROR";
}

export default function WalletPage() {
  const [wallet, setWallet] = useState<WalletDto | null>(null);
  const [amount, setAmount] = useState<string>("100");
  const [msg, setMsg] = useState<string>("");

  const load = async () => {
    setMsg("");
    const res = await api.get("/api/wallet/me");
    setWallet(res.data);
  };

  useEffect(() => {
    load().catch((e) => setMsg(extractErrMsg(e)));
  }, []);

  const deposit = async () => {
    try {
      setMsg("");
      const n = Number(amount);
      if (!Number.isFinite(n) || n <= 0) return setMsg("Amount must be > 0");

      const res = await api.post("/api/wallet/deposit", { amount: n });
      setWallet(res.data);
      setMsg("Deposit success");
    } catch (e: any) {
      setMsg(extractErrMsg(e));
    }
  };

  const withdraw = async () => {
    try {
      setMsg("");
      const n = Number(amount);
      if (!Number.isFinite(n) || n <= 0) return setMsg("Amount must be > 0");

      const res = await api.post("/api/wallet/withdraw", { amount: n });
      setWallet(res.data);
      setMsg("Withdraw success");
    } catch (e: any) {
      setMsg(extractErrMsg(e));
    }
  };

  if (!wallet) return <div style={{ padding: 16 }}>{msg || "Loading..."}</div>;

  return (
    <div style={{ padding: 16 }}>
      <h2>Wallet</h2>
      <div>Balance: {wallet.balance}</div>

      <div style={{ marginTop: 12 }}>
        <input
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          type="number"
          min={0}
          step="0.0001"
        />
        <button onClick={deposit} style={{ marginLeft: 8 }}>Deposit</button>
        <button onClick={withdraw} style={{ marginLeft: 8 }}>Withdraw</button>
      </div>

      {msg && <div style={{ marginTop: 12 }}>{msg}</div>}
    </div>
  );
}
