package com.investmenttracker.server.trades.dto;

import com.investmenttracker.server.trades.TradeType;

import java.math.BigDecimal;

public record TradeCreateRequest(
        String symbol,
        TradeType type,
        BigDecimal quantity
) {}
