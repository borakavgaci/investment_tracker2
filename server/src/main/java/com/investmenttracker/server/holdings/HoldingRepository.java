package com.investmenttracker.server.holdings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HoldingRepository extends JpaRepository<Holding, HoldingId> {
    List<Holding> findAllByIdUserId(UUID userId);
}
