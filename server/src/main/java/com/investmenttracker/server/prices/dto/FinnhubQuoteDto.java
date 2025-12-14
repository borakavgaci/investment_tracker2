package com.investmenttracker.server.prices.dto;

import java.math.BigDecimal;

public record FinnhubQuoteDto(
        BigDecimal c,
        BigDecimal d,
        BigDecimal dp,
        BigDecimal h,
        BigDecimal l,
        BigDecimal o,
        BigDecimal pc,
        Long t
) {}
