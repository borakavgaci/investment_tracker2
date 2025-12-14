package com.investmenttracker.server.wallets;

import com.investmenttracker.server.wallets.dto.WalletAmountRequest;
import com.investmenttracker.server.wallets.dto.WalletDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    private UUID requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("UNAUTHORIZED");
        }
        return UUID.fromString(auth.getName()); // JwtAuthFilter name=userId set ediyor
    }

    @Transactional
    public Wallet ensureWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUserId(userId);
                    w.setBalance(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
                    return walletRepository.save(w);
                });
    }

    @Transactional
    public Wallet ensureMyWallet() {
        return ensureWallet(requireUserId());
    }

    @Transactional(readOnly = true)
    public WalletDto getMyWallet() {
        UUID userId = requireUserId();
        Wallet w = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("WALLET_NOT_FOUND"));

        BigDecimal balance = w.getBalance() == null ? BigDecimal.ZERO : w.getBalance();
        balance = balance.setScale(4, RoundingMode.HALF_UP);

        return new WalletDto(w.getId(), w.getUserId(), balance);
    }

    @Transactional
    public WalletDto deposit(WalletAmountRequest req) {
        Wallet w = ensureMyWallet();

        BigDecimal amount = normalizeAmount(req);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("AMOUNT_MUST_BE_POSITIVE");
        }

        BigDecimal bal = w.getBalance() == null ? BigDecimal.ZERO : w.getBalance();
        bal = bal.add(amount).setScale(4, RoundingMode.HALF_UP);

        w.setBalance(bal);
        walletRepository.save(w);

        return new WalletDto(w.getId(), w.getUserId(), w.getBalance());
    }

    @Transactional
    public WalletDto withdraw(WalletAmountRequest req) {
        Wallet w = ensureMyWallet();

        BigDecimal amount = normalizeAmount(req);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("AMOUNT_MUST_BE_POSITIVE");
        }

        BigDecimal bal = w.getBalance() == null ? BigDecimal.ZERO : w.getBalance();
        if (bal.compareTo(amount) < 0) {
            throw new IllegalArgumentException("INSUFFICIENT_BALANCE");
        }

        bal = bal.subtract(amount).setScale(4, RoundingMode.HALF_UP);

        w.setBalance(bal);
        walletRepository.save(w);

        return new WalletDto(w.getId(), w.getUserId(), w.getBalance());
    }

    private BigDecimal normalizeAmount(WalletAmountRequest req) {
        if (req == null || req.amount() == null) return BigDecimal.ZERO;
        return req.amount().setScale(4, RoundingMode.HALF_UP);
    }
}
