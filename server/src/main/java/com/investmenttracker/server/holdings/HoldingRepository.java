package com.investmenttracker.server.holdings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HoldingRepository extends JpaRepository<Holding, HoldingId> {

    Optional<Holding> findByIdUserIdAndIdStockId(UUID userId, UUID stockId);

    List<Holding> findByIdUserId(UUID userId);
}
