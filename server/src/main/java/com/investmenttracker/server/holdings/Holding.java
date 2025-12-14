package com.investmenttracker.server.holdings;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings")
public class Holding {

    @EmbeddedId
    private HoldingId id;

    @Column(name = "quantity", precision = 18, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(name = "avg_cost", precision = 18, scale = 4, nullable = false)
    private BigDecimal avgCost;

    public Holding() {}

    public Holding(HoldingId id, BigDecimal quantity, BigDecimal avgCost) {
        this.id = id;
        this.quantity = quantity;
        this.avgCost = avgCost;
    }

    public HoldingId getId() { return id; }
    public void setId(HoldingId id) { this.id = id; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAvgCost() { return avgCost; }
    public void setAvgCost(BigDecimal avgCost) { this.avgCost = avgCost; }
}
