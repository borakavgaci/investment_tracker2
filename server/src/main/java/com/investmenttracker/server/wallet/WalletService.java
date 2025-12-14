package com.investmenttracker.server.wallet;

import com.investmenttracker.server.wallet.dto.WalletResponse;
import com.investmenttracker.server.wallet.dto.WalletTxResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

  private static final BigDecimal FIXED_AMOUNT = new BigDecimal("500.0000");

  private final WalletRepository walletRepository;
  private final WalletTransactionRepository txRepository;

  public WalletService(WalletRepository walletRepository, WalletTransactionRepository txRepository) {
    this.walletRepository = walletRepository;
    this.txRepository = txRepository;
  }

  public WalletResponse getMyWallet(UUID userId) {
    Wallet w = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalStateException("WALLET_NOT_FOUND"));

    return new WalletResponse(w.getId(), w.getFreeBalance(), w.getLastUpdated());
  }

  public List<WalletTxResponse> getRecentTransactions(UUID userId) {
    Wallet w = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalStateException("WALLET_NOT_FOUND"));

    return txRepository.findTop20ByWalletIdOrderByTransactionDateDesc(w.getId())
        .stream()
        .map(t -> new WalletTxResponse(t.getId(), t.getWalletId(), t.getType(), t.getAmount(), t.getTransactionDate()))
        .toList();
  }

  @Transactional
  public WalletResponse depositFixed(UUID userId) {
    Wallet w = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalStateException("WALLET_NOT_FOUND"));

    w.setFreeBalance(w.getFreeBalance().add(FIXED_AMOUNT));
    w.setLastUpdated(LocalDateTime.now());
    walletRepository.save(w);

    WalletTransaction tx = new WalletTransaction();
    tx.setWalletId(w.getId());
    tx.setType("deposit");
    tx.setAmount(FIXED_AMOUNT);
    txRepository.save(tx);

    return new WalletResponse(w.getId(), w.getFreeBalance(), w.getLastUpdated());
  }

  @Transactional
  public WalletResponse withdrawFixed(UUID userId) {
    Wallet w = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalStateException("WALLET_NOT_FOUND"));

    if (w.getFreeBalance().compareTo(FIXED_AMOUNT) < 0) {
      throw new IllegalArgumentException("INSUFFICIENT_BALANCE");
    }

    w.setFreeBalance(w.getFreeBalance().subtract(FIXED_AMOUNT));
    w.setLastUpdated(LocalDateTime.now());
    walletRepository.save(w);

    WalletTransaction tx = new WalletTransaction();
    tx.setWalletId(w.getId());
    tx.setType("withdrawal");
    tx.setAmount(FIXED_AMOUNT);
    txRepository.save(tx);

    return new WalletResponse(w.getId(), w.getFreeBalance(), w.getLastUpdated());
  }
}
