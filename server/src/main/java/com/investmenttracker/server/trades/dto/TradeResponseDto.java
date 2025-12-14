package com.investmenttracker.server.trades.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TradeResponseDto(
        UUID tradeId,
        UUID stockId,
        String type,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal walletBalance
) {}
