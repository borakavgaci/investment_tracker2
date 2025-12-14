package com.investmenttracker.server.wallets;

import com.investmenttracker.server.wallets.dto.WalletAmountRequest;
import com.investmenttracker.server.wallets.dto.WalletDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public WalletDto myWallet() {
        return walletService.getMyWallet();
    }

    @PostMapping("/deposit")
    public WalletDto deposit(@RequestBody WalletAmountRequest req) {
        return walletService.deposit(req);
    }

    @PostMapping("/withdraw")
    public WalletDto withdraw(@RequestBody WalletAmountRequest req) {
        return walletService.withdraw(req);
    }
}
