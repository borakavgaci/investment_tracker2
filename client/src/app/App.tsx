import { useEffect } from "react";
import { AppRoutes } from "./routes";
import { useAuthStore } from "../features/auth/auth.store";

export default function App() {
  const hydrate = useAuthStore((s) => s.hydrate);

  useEffect(() => {
    hydrate();
  }, [hydrate]);

  return <AppRoutes />;
}
