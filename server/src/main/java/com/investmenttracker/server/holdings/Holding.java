package com.investmenttracker.server.holdings;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "holdings")
public class Holding {

    @EmbeddedId
    private HoldingId id;

    // NOT NULL önerilir
    @Column(name = "quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;

    // Ortalama maliyet (avg_cost) — yoksa migration gerekir, ama önce compile çözelim
    @Column(name = "avg_cost", precision = 18, scale = 4)
    private BigDecimal avgCost;

    @Column(name = "created_at")
    private Instant createdAt;

    public Holding() {}

    public static Holding create(UUID userId, UUID stockId) {
        Holding h = new Holding();
        h.id = new HoldingId(userId, stockId);
        h.quantity = BigDecimal.ZERO;
        h.avgCost = BigDecimal.ZERO;
        h.createdAt = Instant.now();
        return h;
    }

    public HoldingId getId() { return id; }
    public void setId(HoldingId id) { this.id = id; }

    public UUID getUserId() { return id != null ? id.getUserId() : null; }
    public UUID getStockId() { return id != null ? id.getStockId() : null; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAvgCost() { return avgCost; }
    public void setAvgCost(BigDecimal avgCost) { this.avgCost = avgCost; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
