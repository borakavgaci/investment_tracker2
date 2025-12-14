package com.investmenttracker.server.portfolio;

import com.investmenttracker.server.holdings.Holding;
import com.investmenttracker.server.holdings.HoldingRepository;
import com.investmenttracker.server.portfolio.dto.PortfolioHoldingDto;
import com.investmenttracker.server.portfolio.dto.PortfolioResponseDto;
import com.investmenttracker.server.prices.FinnhubPriceClient;
import com.investmenttracker.server.prices.dto.FinnhubQuoteDto;
import com.investmenttracker.server.stocks.Stock;
import com.investmenttracker.server.stocks.StockRepository;
import com.investmenttracker.server.wallets.Wallet;
import com.investmenttracker.server.wallets.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final WalletRepository walletRepository;
    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final FinnhubPriceClient finnhubPriceClient;

    public PortfolioService(
            WalletRepository walletRepository,
            HoldingRepository holdingRepository,
            StockRepository stockRepository,
            FinnhubPriceClient finnhubPriceClient
    ) {
        this.walletRepository = walletRepository;
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
        this.finnhubPriceClient = finnhubPriceClient;
    }

    public PortfolioResponseDto getMyPortfolio(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("WALLET_NOT_FOUND"));

        List<Holding> hs = holdingRepository.findByIdUserId(userId);

        // stockId -> Stock
        Map<UUID, Stock> stocks = stockRepository.findAllById(
                hs.stream().map(Holding::getStockId).filter(Objects::nonNull).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Stock::getId, s -> s));

        List<PortfolioHoldingDto> out = new ArrayList<>();

        for (Holding h : hs) {
            Stock s = stocks.get(h.getStockId());
            if (s == null) continue;

            BigDecimal qty = nz(h.getQuantity());
            if (qty.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal avg = nz(h.getAvgCost()).setScale(4, RoundingMode.HALF_UP);

            // current price: DB’deki currentValue varsa onu kullan, yoksa Finnhub (limit yoksa patlayabilir)
            BigDecimal current = nz(s.getCurrentValue()).setScale(4, RoundingMode.HALF_UP);
            if (current.compareTo(BigDecimal.ZERO) <= 0 && "US".equalsIgnoreCase(s.getCountry())) {
                try {
                    FinnhubQuoteDto q = finnhubPriceClient.getQuote(s.getSymbol());
                    if (q != null && q.c() != null) current = q.c().setScale(4, RoundingMode.HALF_UP);
                } catch (Exception ignored) {}
            }

            BigDecimal mv = qty.multiply(current).setScale(4, RoundingMode.HALF_UP);
            BigDecimal cost = qty.multiply(avg).setScale(4, RoundingMode.HALF_UP);
            BigDecimal pnl = mv.subtract(cost).setScale(4, RoundingMode.HALF_UP);
            BigDecimal pnlPct = BigDecimal.ZERO;
            if (cost.compareTo(BigDecimal.ZERO) > 0) {
                pnlPct = pnl.divide(cost, 8, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(4, RoundingMode.HALF_UP);
            }

            out.add(new PortfolioHoldingDto(
                    s.getId(),
                    s.getSymbol(),
                    s.getCompanyName(),
                    qty.setScale(4, RoundingMode.HALF_UP),
                    avg,
                    current,
                    mv,
                    pnl,
                    pnlPct
            ));
        }

        return new PortfolioResponseDto(wallet.getId(), wallet.getBalance(), out);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
