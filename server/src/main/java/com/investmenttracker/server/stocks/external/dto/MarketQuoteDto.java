package com.investmenttracker.server.stocks.external.dto;

import java.math.BigDecimal;

public class MarketQuoteDto {
    // current price
    public BigDecimal c;

    // daily change
    public BigDecimal d;

    // percent change
    public BigDecimal dp;

    // high price of the day
    public BigDecimal h;

    // low price of the day
    public BigDecimal l;

    // open price of the day
    public BigDecimal o;

    // previous close price
    public BigDecimal pc;

    // timestamp
    public Long t;
}
