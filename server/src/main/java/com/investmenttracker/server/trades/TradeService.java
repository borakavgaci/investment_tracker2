package com.investmenttracker.server.trades;

import com.investmenttracker.server.holdings.Holding;
import com.investmenttracker.server.holdings.HoldingId;
import com.investmenttracker.server.holdings.HoldingRepository;
import com.investmenttracker.server.stocks.Stock;
import com.investmenttracker.server.stocks.StockRepository;
import com.investmenttracker.server.trades.dto.TradeCreateRequest;
import com.investmenttracker.server.wallets.Wallet;
import com.investmenttracker.server.wallets.WalletRepository;
import com.investmenttracker.server.wallets.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
public class TradeService {

    private final StockRepository stockRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final TradeRepository tradeRepository;
    private final HoldingRepository holdingRepository;

    public TradeService(
            StockRepository stockRepository,
            WalletService walletService,
            WalletRepository walletRepository,
            TradeRepository tradeRepository,
            HoldingRepository holdingRepository
    ) {
        this.stockRepository = stockRepository;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.tradeRepository = tradeRepository;
        this.holdingRepository = holdingRepository;
    }

    @Transactional
    public Trade create(TradeCreateRequest req) {
        if (req == null) throw new IllegalArgumentException("INVALID_REQUEST");
        if (req.symbol() == null || req.symbol().isBlank()) throw new IllegalArgumentException("SYMBOL_REQUIRED");
        if (req.type() == null) throw new IllegalArgumentException("TYPE_REQUIRED");

        BigDecimal qty = req.quantity() == null ? BigDecimal.ZERO : req.quantity();
        qty = qty.setScale(4, RoundingMode.HALF_UP);
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("QUANTITY_MUST_BE_POSITIVE");

        Stock stock = stockRepository.findBySymbolIgnoreCase(req.symbol().trim())
                .orElseThrow(() -> new IllegalArgumentException("STOCK_NOT_FOUND"));

        BigDecimal price = stock.getCurrentValue();
        if (price == null) throw new IllegalStateException("STOCK_PRICE_NOT_AVAILABLE");
        price = price.setScale(4, RoundingMode.HALF_UP);

        Wallet wallet = walletService.ensureMyWallet();

        BigDecimal bal = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        bal = bal.setScale(4, RoundingMode.HALF_UP);

        BigDecimal total = price.multiply(qty).setScale(4, RoundingMode.HALF_UP);

        HoldingId hid = new HoldingId(wallet.getUserId(), stock.getId());
        Holding holding = holdingRepository.findById(hid).orElse(null);

        if (req.type() == TradeType.BUY) {
            if (bal.compareTo(total) < 0) throw new IllegalArgumentException("INSUFFICIENT_BALANCE");
            wallet.setBalance(bal.subtract(total).setScale(4, RoundingMode.HALF_UP));

            if (holding == null) {
                holding = new Holding(hid, qty, price);
            } else {
                BigDecimal oldQty = holding.getQuantity().setScale(4, RoundingMode.HALF_UP);
                BigDecimal oldAvg = holding.getAvgCost().setScale(4, RoundingMode.HALF_UP);
                BigDecimal newQty = oldQty.add(qty).setScale(4, RoundingMode.HALF_UP);

                BigDecimal newAvg = oldQty.multiply(oldAvg)
                        .add(qty.multiply(price))
                        .divide(newQty, 4, RoundingMode.HALF_UP);

                holding.setQuantity(newQty);
                holding.setAvgCost(newAvg);
            }

            holdingRepository.save(holding);

        } else { // SELL
            if (holding == null) throw new IllegalArgumentException("INSUFFICIENT_HOLDING");
            BigDecimal oldQty = holding.getQuantity().setScale(4, RoundingMode.HALF_UP);
            if (oldQty.compareTo(qty) < 0) throw new IllegalArgumentException("INSUFFICIENT_HOLDING");

            wallet.setBalance(bal.add(total).setScale(4, RoundingMode.HALF_UP));

            BigDecimal newQty = oldQty.subtract(qty).setScale(4, RoundingMode.HALF_UP);
            if (newQty.compareTo(BigDecimal.ZERO) == 0) {
                holdingRepository.delete(holding);
            } else {
                holding.setQuantity(newQty);
                holdingRepository.save(holding);
            }
        }

        walletRepository.save(wallet);

        Trade t = new Trade();
        t.setUserId(wallet.getUserId());
        t.setWalletId(wallet.getId());
        t.setStockId(stock.getId());
        t.setType(req.type());
        t.setQuantity(qty);
        t.setPrice(price);
        t.setTradeDatetime(Instant.now());

        return tradeRepository.save(t);
    }
}
