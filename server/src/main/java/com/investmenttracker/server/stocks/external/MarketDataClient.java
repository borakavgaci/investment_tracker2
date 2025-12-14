package com.investmenttracker.server.stocks.external;

import com.investmenttracker.server.stocks.external.dto.MarketSymbolDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import com.investmenttracker.server.stocks.external.dto.MarketQuoteDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class MarketDataClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${market.finnhub.base-url}")
    private String baseUrl;

    @Value("${market.finnhub.api-key}")
    private String apiKey;

    public List<MarketSymbolDto> getSymbolsByExchange(String exchangeCode) {
  String url = baseUrl + "/stock/symbol?exchange=" + exchangeCode + "&token=" + apiKey;

  try {
    MarketSymbolDto[] res = restTemplate.getForObject(url, MarketSymbolDto[].class);
    return List.of(res == null ? new MarketSymbolDto[0] : res);
  } catch (Exception e) {
    throw new IllegalStateException("FINNHUB_ERROR: " + e.getMessage());
  }
}
public MarketQuoteDto getQuote(String symbol) {
    String encoded = URLEncoder.encode(symbol, StandardCharsets.UTF_8);
    String url = baseUrl + "/quote?symbol=" + encoded + "&token=" + apiKey;

    try {
        return restTemplate.getForObject(url, MarketQuoteDto.class);
    } catch (Exception e) {
        throw new IllegalStateException("FINNHUB_ERROR_QUOTE: " + e.getMessage());
    }
}


}
