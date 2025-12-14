package com.investmenttracker.server.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioResponseDto(
        UUID walletId,
        BigDecimal balance,
        List<PortfolioHoldingDto> holdings
) {}
