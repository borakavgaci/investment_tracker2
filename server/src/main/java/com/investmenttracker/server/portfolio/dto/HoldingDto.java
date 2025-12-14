package com.investmenttracker.server.portfolio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HoldingDto(
        UUID stockId,
        String symbol,
        String companyName,
        BigDecimal quantity,
        BigDecimal avgCost,
        BigDecimal currentPrice,
        BigDecimal marketValue,
        BigDecimal costBasis,
        BigDecimal pnl,
        BigDecimal pnlPercent
) {}
