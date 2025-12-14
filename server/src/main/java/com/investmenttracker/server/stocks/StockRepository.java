package com.investmenttracker.server.stocks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, UUID> {
  Optional<Stock> findBySymbol(String symbol);
  List<Stock> findByCountry(String country);
  Optional<Stock> findBySymbolIgnoreCase(String symbol);

}
