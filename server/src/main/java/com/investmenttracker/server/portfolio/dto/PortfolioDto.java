package com.investmenttracker.server.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioDto(
        UUID walletId,
        BigDecimal balance,
        BigDecimal totalMarketValue,
        BigDecimal totalCostBasis,
        BigDecimal totalPnl,
        List<HoldingDto> holdings
) {}
