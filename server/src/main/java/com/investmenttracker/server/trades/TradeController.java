package com.investmenttracker.server.trades;

import com.investmenttracker.server.trades.dto.TradeCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    // Frontend şu an /api/trades'e POST atıyor -> BU ENDPOINT ŞART
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TradeCreateRequest req) {
        return ResponseEntity.ok(tradeService.create(req));
    }
}
