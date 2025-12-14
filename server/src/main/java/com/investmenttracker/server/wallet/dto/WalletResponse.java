package com.investmenttracker.server.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletResponse {
  public UUID walletId;
  public BigDecimal freeBalance;
  public LocalDateTime lastUpdated;

  public WalletResponse(UUID walletId, BigDecimal freeBalance, LocalDateTime lastUpdated) {
    this.walletId = walletId;
    this.freeBalance = freeBalance;
    this.lastUpdated = lastUpdated;
  }
}
