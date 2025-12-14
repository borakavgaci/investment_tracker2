import { useEffect } from "react";
import { AppRoutes } from "./routes";
import { useAuthStore } from "../features/auth/auth.store";

export default function App() {
  const hydrate = useAuthStore((s) => s.hydrate);
  const hydrated = useAuthStore((s) => s.hydrated);

  useEffect(() => {
    // Auth state'i localStorage vb. kaynaktan 1 kere hydrate et
    if (!hydrated) hydrate();
  }, [hydrated, hydrate]);

  // Routing burada değil; AppRoutes içinde yönetiliyor
  return <AppRoutes />;
}
