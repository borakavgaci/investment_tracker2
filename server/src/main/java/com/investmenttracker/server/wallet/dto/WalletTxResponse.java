package com.investmenttracker.server.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletTxResponse {
  public UUID id;
  public UUID walletId;
  public String type;
  public BigDecimal amount;
  public LocalDateTime transactionDate;

  public WalletTxResponse(UUID id, UUID walletId, String type, BigDecimal amount, LocalDateTime transactionDate) {
    this.id = id;
    this.walletId = walletId;
    this.type = type;
    this.amount = amount;
    this.transactionDate = transactionDate;
  }
}
