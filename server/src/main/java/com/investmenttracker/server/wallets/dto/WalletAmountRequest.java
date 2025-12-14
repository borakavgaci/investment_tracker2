package com.investmenttracker.server.wallets.dto;

import java.math.BigDecimal;

public record WalletAmountRequest(
        BigDecimal amount
) {}
