package com.investmenttracker.server.auth;

import com.investmenttracker.server.auth.dto.LoginRequest;
import com.investmenttracker.server.auth.dto.RegisterRequest;
import com.investmenttracker.server.user.User;
import com.investmenttracker.server.user.UserRepository;
import com.investmenttracker.server.wallets.WalletService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public AuthService(
            UserRepository userRepository,
            WalletService walletService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.jwtService = jwtService;
    }

    @Transactional
    public String login(LoginRequest req) {
        String email = req.email.trim().toLowerCase();

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!encoder.matches(req.password, u.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        // Login'de de wallet'ı garanti et (wallet tablosu/record'ı eksik kalmış kullanıcıları otomatik düzeltir)
        walletService.ensureWallet(u.getId());

        return jwtService.generateToken(u.getId(), u.getEmail());
    }

    @Transactional
    public User register(RegisterRequest req) {
        String email = req.email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS");
        }

        User u = new User();
        u.setName(req.name.trim());
        u.setSurname(req.surname.trim());
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password));
        u.setAdmin(false);

        User saved = userRepository.save(u);

        // Register sonrası wallet kesin oluştur
        walletService.ensureWallet(saved.getId());

        return saved;
    }
}
