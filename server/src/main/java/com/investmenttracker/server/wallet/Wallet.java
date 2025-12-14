package com.investmenttracker.server.wallet;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "user_id", columnDefinition = "uuid", nullable = false, unique = true)
  private UUID userId;

  @Column(name = "free_balance", nullable = false, precision = 18, scale = 4)
  private BigDecimal freeBalance = BigDecimal.ZERO;

  @Column(name = "last_updated", nullable = false)
  private LocalDateTime lastUpdated;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (lastUpdated == null) lastUpdated = LocalDateTime.now();
    if (freeBalance == null) freeBalance = BigDecimal.ZERO;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }

  public BigDecimal getFreeBalance() { return freeBalance; }
  public void setFreeBalance(BigDecimal freeBalance) { this.freeBalance = freeBalance; }

  public LocalDateTime getLastUpdated() { return lastUpdated; }
  public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
