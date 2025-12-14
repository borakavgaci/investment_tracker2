package com.investmenttracker.server.holdings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class HoldingId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "stock_id", nullable = false)
    private UUID stockId;

    public HoldingId() {}

    public HoldingId(UUID userId, UUID stockId) {
        this.userId = userId;
        this.stockId = stockId;
    }

    public UUID getUserId() { return userId; }
    public UUID getStockId() { return stockId; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setStockId(UUID stockId) { this.stockId = stockId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoldingId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(stockId, that.stockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, stockId);
    }
}
