package com.investmenttracker.server.stocks;

import com.investmenttracker.server.prices.FinnhubPriceClient;
import com.investmenttracker.server.prices.dto.FinnhubQuoteDto;
import com.investmenttracker.server.stocks.dto.StockResponseDto;
import com.investmenttracker.server.stocks.external.MarketDataClient;
import com.investmenttracker.server.stocks.external.dto.MarketSymbolDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import com.investmenttracker.server.prices.dto.FinnhubProfileDto;
import com.investmenttracker.server.stocks.dto.StockDetailResponseDto;

import com.investmenttracker.server.prices.dto.FinnhubProfileDto;
import com.investmenttracker.server.stocks.dto.StockDetailResponseDto;


@Service
public class StockService {

    private final StockRepository stockRepository;
    private final MarketDataClient marketClient;
    private final MarketExchangeMap exchangeMap;
    private final FinnhubPriceClient finnhubPriceClient;

    // Quote refresh interval (429 yememek için çok kritik)
    private static final Duration PRICE_TTL = Duration.ofMinutes(30);

    public StockService(
            StockRepository stockRepository,
            MarketDataClient marketClient,
            MarketExchangeMap exchangeMap,
            FinnhubPriceClient finnhubPriceClient
    ) {
        this.stockRepository = stockRepository;
        this.marketClient = marketClient;
        this.exchangeMap = exchangeMap;
        this.finnhubPriceClient = finnhubPriceClient;
    }

    public List<StockResponseDto> syncAndGetByCountry(String country) {
        return syncAndGetByCountry(country, 50);
    }

    public List<StockResponseDto> syncAndGetByCountry(String country, int limit) {
        String c = country == null ? "" : country.trim().toUpperCase();
        int safeLimit = Math.max(1, Math.min(limit, 50)); // 1..50

        // 1) Önce DB’den dön (her ülke için)
        List<Stock> existing = stockRepository.findByCountry(c);
        if (existing != null && !existing.isEmpty()) {
            // US değilse direkt DB’den dön
            if (!"US".equals(c)) {
                return existing.stream()
                        .limit(safeLimit)
                        .map(this::toDto)
                        .toList();
            }
        } else {
            // DB boşsa ve US değilse upstream'e gitmeyelim (plan kısıtları)
            if (!"US".equals(c)) {
                throw new IllegalArgumentException("NO_DATA_FOR_COUNTRY");
            }
        }

        // 2) US dışında upstream yok (plan kısıtları)
        if (!"US".equals(c)) {
            throw new IllegalArgumentException("NO_DATA_FOR_COUNTRY");
        }

        // 3) US için: önce DB’den limit kadar dönmek üzere listeyi hazırla
        List<Stock> dbList = (existing == null ? List.<Stock>of() : existing)
                .stream()
                .limit(safeLimit)
                .toList();

        // Eğer DB’de yeterli veri yoksa, önce sembolleri çekip DB’ye ekle (fiyat 0 olabilir)
        if (dbList.size() < safeLimit) {
            String exchange = exchangeMap.resolveExchangeOrThrow(c);

            final List<MarketSymbolDto> symbols;
            try {
                symbols = marketClient.getSymbolsByExchange(exchange);
            } catch (Exception ex) {
                throw new IllegalStateException("MARKET_DATA_UNAVAILABLE: " + ex.getMessage());
            }

            int inserted = 0;
            for (MarketSymbolDto s : symbols) {
                if (inserted >= safeLimit) break;
                if (s == null || s.symbol == null) continue;

                String sym = s.symbol.trim();
                if (sym.isEmpty()) continue;

                // varsa geç, yoksa insert et
                if (stockRepository.findBySymbol(sym).isPresent()) continue;

                Stock stock = new Stock();
                if (stock.getId() == null) stock.setId(UUID.randomUUID());
                stock.setSymbol(sym);
                stock.setCompanyName((s.description != null && !s.description.isBlank()) ? s.description : sym);
                stock.setCountry(c);
                stock.setCreatedAt(Instant.now());

                // NOT NULL şartı
                stock.setCurrentValue(new BigDecimal("0.0000"));
                stock.setLastPriceUpdatedAt(null);

                try {
                    stockRepository.save(stock);
                    inserted++;
                } catch (DataIntegrityViolationException e) {
                    // aynı anda başka request insert ettiyse sorun değil
                }
            }

            dbList = stockRepository.findByCountry(c).stream().limit(safeLimit).toList();
        }

        // 4) US için: stale olanları + fiyatı 0 olanları öncelikli güncelle
        Instant now = Instant.now();
        AtomicInteger quoteCalls = new AtomicInteger(0);

        // 10 yerine daha yüksek ama hala kontrollü limit:
        // - safeLimit <= 50
        // - max 25: 429 riskini sınırlamak için
        int maxQuoteCalls = Math.min(safeLimit, 25);

        for (Stock stock : dbList) {
            if (stock == null || stock.getSymbol() == null) continue;

            // 0 fiyat kontrolü (yeni insert edilenler genelde 0)
            BigDecimal cv = stock.getCurrentValue();
            boolean isZero = (cv == null) || (cv.compareTo(BigDecimal.ZERO) == 0);

            // stale kontrol
            Instant last = stock.getLastPriceUpdatedAt();
            boolean stale = (last == null) || Duration.between(last, now).compareTo(PRICE_TTL) > 0;

            // Güncelleme kuralı:
            // - fiyat 0 ise mutlaka dene
            // - değilse stale ise dene
            if (!isZero && !stale) continue;

            // quote çağrı limit (429 riskini azaltmak için)
            if (quoteCalls.get() >= maxQuoteCalls) break;

            String sym = stock.getSymbol().trim();
            if (sym.isEmpty()) continue;

            try {
                System.out.println("FINNHUB_QUOTE_CALL " + sym);

                FinnhubQuoteDto q = finnhubPriceClient.getQuote(sym);

                BigDecimal price = BigDecimal.ZERO;
                if (q != null && q.c() != null) {
                    price = q.c();
                }

                // Eğer Finnhub bu symbol için fiyat döndürmediyse (c null/0), DB’yi 0’a kilitlemeyelim:
                // - Yine de lastPriceUpdatedAt set etmiyoruz ki sonraki request’te tekrar denesin.
                if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
                    System.out.println("FINNHUB_QUOTE_EMPTY " + sym + " c=" + price);
                    continue;
                }

                price = price.setScale(4, RoundingMode.HALF_UP);

                stock.setCurrentValue(price);
                stock.setLastPriceUpdatedAt(now);
                stockRepository.save(stock);

                System.out.println("FINNHUB_QUOTE_OK " + sym + " c=" + price);
                quoteCalls.incrementAndGet();

            } catch (Exception ex) {
                // 429 / network / parse vs: dur ve DB’dekini döndür
                System.out.println("FINNHUB_QUOTE_FAIL " + sym + " -> " + ex.getMessage());
                break;
            }
        }

        // 5) Sonuç: DB’den dön (frontend’e) - DTO formatında
        return stockRepository.findByCountry(c).stream()
                .limit(safeLimit)
                .map(this::toDto)
                .toList();
    }

    private StockResponseDto toDto(Stock s) {
        return new StockResponseDto(
                s.getId(),
                s.getSymbol(),
                s.getCompanyName(),
                s.getCountry(),
                s.getCurrentValue(),
                s.getLastPriceUpdatedAt(),
                s.getCreatedAt()
        );
    }
    public StockDetailResponseDto getStockDetail(String symbol) {
    if (symbol == null || symbol.isBlank()) {
        throw new IllegalArgumentException("SYMBOL_REQUIRED");
    }

    String sym = symbol.trim().toUpperCase();

    Stock stock = stockRepository.findBySymbol(sym)
            .orElseThrow(() -> new IllegalArgumentException("STOCK_NOT_FOUND"));

    FinnhubQuoteDto q = null;
    try { q = finnhubPriceClient.getQuote(sym); } catch (Exception ignored) {}

    FinnhubProfileDto p = null;
    try { p = finnhubPriceClient.getProfile(sym); } catch (Exception ignored) {}

    BigDecimal current = (q != null && q.c() != null) ? q.c() : stock.getCurrentValue();
    BigDecimal open = (q != null) ? q.o() : null;
    BigDecimal high = (q != null) ? q.h() : null;
    BigDecimal low  = (q != null) ? q.l() : null;
    BigDecimal prevClose = (q != null) ? q.pc() : null;

    BigDecimal change = (q != null) ? q.d() : null;
    BigDecimal changePercent = (q != null) ? q.dp() : null;

    Instant quoteTime = null;
    if (q != null && q.t() != null && q.t() > 0) {
        quoteTime = Instant.ofEpochSecond(q.t());
    }

    return new StockDetailResponseDto(
            stock.getId(),
            stock.getSymbol(),
            stock.getCompanyName(),
            stock.getCountry(),

            current,
            open,
            high,
            low,
            prevClose,
            change,
            changePercent,
            quoteTime,

            p != null ? p.exchange() : null,
            p != null ? p.finnhubIndustry() : null,
            p != null ? p.weburl() : null,
            p != null ? p.logo() : null,
            p != null ? p.currency() : null,

            stock.getLastPriceUpdatedAt(),
            stock.getCreatedAt()
    );
}

}
