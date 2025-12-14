import { Navigate, Route, Routes } from "react-router-dom";

import { AuthLayout } from "./layout/AuthLayout";
import { MainLayout } from "./layout/MainLayout";
import { ProtectedRoute } from "./routes/ProtectedRoute";

import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import MainPage from "../pages/main/MainPage";
import PortfolioPage from "../pages/portfolio/PortfolioPage";
import WalletPage from "../pages/wallet/WalletPage";

export function AppRoutes() {
  return (
    <Routes>
      {/* AUTH ROUTES – herkese açık */}
      <Route path="/auth" element={<AuthLayout />}>
        <Route index element={<Navigate to="/auth/login" replace />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
      </Route>

      {/* PROTECTED ROUTES – login şart */}
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Navigate to="/main" replace />} />
          <Route path="main" element={<MainPage />} />
          <Route path="portfolio" element={<PortfolioPage />} />
          <Route path="wallet" element={<WalletPage />} />
        </Route>
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/auth/login" replace />} />
    </Routes>
  );
}
