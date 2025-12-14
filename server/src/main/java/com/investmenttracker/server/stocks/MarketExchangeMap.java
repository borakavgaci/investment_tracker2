package com.investmenttracker.server.stocks;

import org.springframework.stereotype.Component;

@Component
public class MarketExchangeMap {

  public String resolveExchangeOrThrow(String country) {
    if (country == null || country.isBlank()) {
      throw new IllegalArgumentException("COUNTRY_REQUIRED");
    }

    String c = country.trim().toUpperCase();

    return switch (c) {
      case "US" -> "US";
      default -> throw new IllegalArgumentException("COUNTRY_NOT_SUPPORTED_BY_UPSTREAM");
    };
  }
}
