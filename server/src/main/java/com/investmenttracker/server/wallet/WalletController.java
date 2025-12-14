package com.investmenttracker.server.wallet;

import com.investmenttracker.server.user.User;
import com.investmenttracker.server.wallet.dto.WalletResponse;
import com.investmenttracker.server.wallet.dto.WalletTxResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

  private final WalletService walletService;

  public WalletController(WalletService walletService) {
    this.walletService = walletService;
  }

  private UUID userId(Authentication auth) {
    User user = (User) auth.getPrincipal();
    return user.getId();
  }

  @GetMapping("/me")
  public ResponseEntity<WalletResponse> me(Authentication auth) {
    return ResponseEntity.ok(walletService.getMyWallet(userId(auth)));
  }

  @GetMapping("/transactions")
  public ResponseEntity<List<WalletTxResponse>> recent(Authentication auth) {
    return ResponseEntity.ok(walletService.getRecentTransactions(userId(auth)));
  }

  @PostMapping("/deposit")
  public ResponseEntity<WalletResponse> deposit(Authentication auth) {
    return ResponseEntity.ok(walletService.depositFixed(userId(auth)));
  }

  @PostMapping("/withdraw")
  public ResponseEntity<WalletResponse> withdraw(Authentication auth) {
    return ResponseEntity.ok(walletService.withdrawFixed(userId(auth)));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArg(IllegalArgumentException ex) {
    if ("INSUFFICIENT_BALANCE".equals(ex.getMessage())) {
      return ResponseEntity.badRequest().body(Map.of(
          "code", "INSUFFICIENT_BALANCE",
          "message", "Free balance is insufficient for withdrawal."
      ));
    }
    return ResponseEntity.badRequest().body(Map.of("code", "BAD_REQUEST", "message", ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
    if ("WALLET_NOT_FOUND".equals(ex.getMessage())) {
      return ResponseEntity.status(404).body(Map.of("code", "WALLET_NOT_FOUND", "message", "Wallet not found."));
    }
    return ResponseEntity.status(500).body(Map.of("code", "SERVER_ERROR", "message", ex.getMessage()));
  }
}
