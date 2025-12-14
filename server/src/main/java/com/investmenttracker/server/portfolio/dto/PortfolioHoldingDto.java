package com.investmenttracker.server.portfolio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PortfolioHoldingDto(
        UUID stockId,
        String symbol,
        String companyName,
        BigDecimal quantity,
        BigDecimal avgCost,
        BigDecimal currentPrice,
        BigDecimal marketValue,
        BigDecimal unrealizedPnL,
        BigDecimal unrealizedPnLPct
) {}
