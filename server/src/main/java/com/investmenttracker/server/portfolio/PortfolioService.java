package com.investmenttracker.server.portfolio;

import com.investmenttracker.server.common.AuthUtil;
import com.investmenttracker.server.holdings.Holding;
import com.investmenttracker.server.holdings.HoldingRepository;
import com.investmenttracker.server.portfolio.dto.PortfolioDto;
import com.investmenttracker.server.portfolio.dto.PortfolioHoldingDto;
import com.investmenttracker.server.stocks.Stock;
import com.investmenttracker.server.stocks.StockRepository;
import com.investmenttracker.server.wallets.Wallet;
import com.investmenttracker.server.wallets.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PortfolioService {

    private final WalletService walletService;
    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;

    public PortfolioService(
            WalletService walletService,
            HoldingRepository holdingRepository,
            StockRepository stockRepository
    ) {
        this.walletService = walletService;
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioDto getMyPortfolio() {
        UUID userId = AuthUtil.requireUserId();

        Wallet w = walletService.ensureWallet(userId);

        BigDecimal balance = w.getBalance() == null ? BigDecimal.ZERO : w.getBalance();
        balance = balance.setScale(4, RoundingMode.HALF_UP);

        List<Holding> hs = holdingRepository.findAllByIdUserId(userId);

        List<PortfolioHoldingDto> out = new ArrayList<>();
        for (Holding h : hs) {
            UUID stockId = h.getId().getStockId();
            Stock s = stockRepository.findById(stockId).orElse(null);

            out.add(new PortfolioHoldingDto(
                    s == null ? null : s.getSymbol(),
                    s == null ? null : s.getCompanyName(),
                    (h.getQuantity() == null ? BigDecimal.ZERO : h.getQuantity()).setScale(4, RoundingMode.HALF_UP),
                    (h.getAvgCost() == null ? BigDecimal.ZERO : h.getAvgCost()).setScale(4, RoundingMode.HALF_UP)
            ));
        }

        return new PortfolioDto(balance, out);
    }
}
