package com.investmenttracker.server.portfolio.dto;

import java.math.BigDecimal;

public record PortfolioHoldingDto(
        String symbol,
        String companyName,
        BigDecimal quantity,
        BigDecimal avgCost
) {}
