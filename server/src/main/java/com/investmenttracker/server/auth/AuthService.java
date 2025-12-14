package com.investmenttracker.server.auth;

import com.investmenttracker.server.auth.dto.RegisterRequest;
import com.investmenttracker.server.user.User;
import com.investmenttracker.server.user.UserRepository;
import com.investmenttracker.server.wallet.Wallet;
import com.investmenttracker.server.wallet.WalletRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.investmenttracker.server.auth.dto.LoginRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, WalletRepository walletRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest req) {
        String email = req.email.trim().toLowerCase();

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!encoder.matches(req.password, u.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

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

        Wallet w = new Wallet();
        w.setUserId(saved.getId());
        walletRepository.save(w);

        return saved;
    }
}
