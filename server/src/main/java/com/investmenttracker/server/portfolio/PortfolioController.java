package com.investmenttracker.server.portfolio;

import com.investmenttracker.server.auth.AuthUser;
import com.investmenttracker.server.portfolio.dto.PortfolioResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/me")
    public PortfolioResponseDto me() {
        UUID userId = AuthUser.requireUserId();
        return portfolioService.getMyPortfolio(userId);
    }
}
