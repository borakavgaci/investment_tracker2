package com.investmenttracker.server.stocks;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;

import java.time.Instant;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "current_value", precision = 18, scale = 4)
    private BigDecimal currentValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "last_price_updated_at")
    private Instant lastPriceUpdatedAt;

    public Stock() {
    }

    // GETTERS
    public UUID getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCountry() {
        return country;
    }

    public Instant getLastPriceUpdatedAt() {
        return lastPriceUpdatedAt;
    }

    // SETTERS
    public void setId(UUID id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLastPriceUpdatedAt(Instant lastPriceUpdatedAt) {
        this.lastPriceUpdatedAt = lastPriceUpdatedAt;
    }
}
