package com.investmenttracker.server.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioDto(
        BigDecimal balance,
        List<PortfolioHoldingDto> holdings
) {}
