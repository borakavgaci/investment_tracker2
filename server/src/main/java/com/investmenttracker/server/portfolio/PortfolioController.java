package com.investmenttracker.server.portfolio;

import com.investmenttracker.server.portfolio.dto.PortfolioDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/me")
    public PortfolioDto me() {
        return portfolioService.getMyPortfolio();
    }
}
