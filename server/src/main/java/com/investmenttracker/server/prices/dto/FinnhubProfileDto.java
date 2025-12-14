package com.investmenttracker.server.prices.dto;

public record FinnhubProfileDto(
        String country,
        String currency,
        String exchange,
        String finnhubIndustry,
        String ipo,
        String logo,
        Double marketCapitalization,
        String name,
        String phone,
        Double shareOutstanding,
        String ticker,
        String weburl
) {}
