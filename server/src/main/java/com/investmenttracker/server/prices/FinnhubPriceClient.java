package com.investmenttracker.server.prices;

import com.investmenttracker.server.prices.dto.FinnhubProfileDto;
import com.investmenttracker.server.prices.dto.FinnhubQuoteDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.investmenttracker.server.prices.dto.FinnhubProfileDto;



@Service
public class FinnhubPriceClient {

    private final WebClient webClient;
    private final String token;

    public FinnhubPriceClient(
            WebClient.Builder builder,
            @Value("${prices.finnhub.base-url}") String baseUrl,
            @Value("${prices.finnhub.token}") String token
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.token = token;
    }

    public FinnhubQuoteDto getQuote(String symbol) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", token)
                            .build())
                    .retrieve()
                    .bodyToMono(FinnhubQuoteDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            // API 4xx/5xx vb.
            throw new IllegalStateException(
                    "Finnhub quote request failed. symbol=" + symbol
                            + ", status=" + e.getStatusCode()
                            + ", body=" + e.getResponseBodyAsString(),
                    e
            );
        } catch (Exception e) {
            // network/parse/timeouts vb.
            throw new IllegalStateException("Finnhub quote request failed. symbol=" + symbol, e);
        }
    }

    public FinnhubProfileDto getProfile(String symbol) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/stock/profile2")
                    .queryParam("symbol", symbol)
                    .queryParam("token", token)
                    .build())
            .retrieve()
            .bodyToMono(FinnhubProfileDto.class)
            .block();
}


}
