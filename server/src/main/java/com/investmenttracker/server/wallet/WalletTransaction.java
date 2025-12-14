package com.investmenttracker.server.wallet;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "wallet_id", columnDefinition = "uuid", nullable = false)
  private UUID walletId;

  @Column(nullable = false, length = 30)
  private String type; // "deposit" / "withdrawal"

  @Column(nullable = false, precision = 18, scale = 4)
  private BigDecimal amount;

  @Column(name = "transaction_date", nullable = false)
  private LocalDateTime transactionDate;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (transactionDate == null) transactionDate = LocalDateTime.now();
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public UUID getWalletId() { return walletId; }
  public void setWalletId(UUID walletId) { this.walletId = walletId; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }

  public LocalDateTime getTransactionDate() { return transactionDate; }
  public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
