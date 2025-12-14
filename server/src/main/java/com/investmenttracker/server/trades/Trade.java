package com.investmenttracker.server.trades;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "stock_id", nullable = false)
    private UUID stockId;

    // Postgres enum (trade_type) ile doğru JDBC binding
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false, columnDefinition = "trade_type")
    private TradeType type;

    @Column(name = "quantity", precision = 18, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(name = "price", precision = 18, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "trade_datetime", nullable = false)
    private Instant tradeDatetime = Instant.now();

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public UUID getStockId() { return stockId; }
    public void setStockId(UUID stockId) { this.stockId = stockId; }

    public TradeType getType() { return type; }
    public void setType(TradeType type) { this.type = type; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Instant getTradeDatetime() { return tradeDatetime; }
    public void setTradeDatetime(Instant tradeDatetime) { this.tradeDatetime = tradeDatetime; }
}
