package com.investmenttracker.server.wallets.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletDto(
        UUID id,
        UUID userId,
        BigDecimal balance
) {}
