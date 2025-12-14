package com.investmenttracker.server.stocks;

import com.investmenttracker.server.stocks.dto.StockResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.investmenttracker.server.stocks.dto.StockDetailResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import com.investmenttracker.server.stocks.dto.StockDetailResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockResponseDto> getStocks(
            @RequestParam String country,
            @RequestParam(defaultValue = "50") int limit) {
        System.out.println("STOCKS_ENDPOINT_HIT country=" + country + " limit=" + limit);
        int safeLimit = Math.max(1, Math.min(limit, 50));
        return stockService.syncAndGetByCountry(country, safeLimit);
    }

    @GetMapping("/{symbol}")
    public StockDetailResponseDto getStockDetail(@PathVariable String symbol) {
        return stockService.getStockDetail(symbol);
    }

}
