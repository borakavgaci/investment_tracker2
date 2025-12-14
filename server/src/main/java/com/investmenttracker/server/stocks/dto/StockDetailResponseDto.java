package com.investmenttracker.server.stocks.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockDetailResponseDto(
        UUID id,
        String symbol,
        String companyName,
        String country,

        BigDecimal currentValue,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal prevClose,
        BigDecimal change,
        BigDecimal changePercent,
        Instant quoteTime,

        String exchange,
        String industry,
        String website,
        String logo,
        String currency,

        Instant lastPriceUpdatedAt,
        Instant createdAt
) {}
