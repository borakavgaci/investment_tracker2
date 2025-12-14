package com.investmenttracker.server.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

  private final Key key;
  private final int expMinutes;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expMinutes}") int expMinutes
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expMinutes = expMinutes;
  }

  public String generateToken(UUID userId, String email) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds((long) expMinutes * 60);

    return Jwts.builder()
        .subject(userId.toString())
        .claim("email", email)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith((javax.crypto.SecretKey) key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public UUID getUserId(String token) {
    return UUID.fromString(parseClaims(token).getSubject());
  }

  public String getEmail(String token) {
    Object v = parseClaims(token).get("email");
    return v == null ? null : v.toString();
  }
}
