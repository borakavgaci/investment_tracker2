package com.investmenttracker.server.stocks.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockResponseDto(
        UUID id,
        String symbol,
        String companyName,
        String country,
        BigDecimal currentValue,
        Instant lastPriceUpdatedAt,
        Instant createdAt
) {}
