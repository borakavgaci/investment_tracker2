package com.investmenttracker.server.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final int expMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expMinutes:120}") int expMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMinutes = expMinutes;
    }

    public String generateToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60L);

        return Jwts.builder()
                // KRİTİK: subject = userId (UUID)
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** JwtAuthFilter'ın beklediği metod */
    public UUID extractUserId(String token) {
        Claims claims = parseClaims(token);
        String sub = claims.getSubject();
        return UUID.fromString(sub);
    }

    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        Object email = claims.get("email");
        return email == null ? null : String.valueOf(email);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp == null || exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
