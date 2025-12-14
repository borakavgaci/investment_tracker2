package com.investmenttracker.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // ✅ CORS aktif
      .cors(cors -> {})

      // ✅ CSRF kapalı (REST)
      .csrf(csrf -> csrf.disable())

      // ✅ Stateless
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

      // ✅ Token yoksa 401
      .exceptionHandling(eh ->
        eh.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
      )

      // ✅ YETKİ KURALLARI
      .authorizeHttpRequests(auth -> auth

        // 🔥 CORS PREFLIGHT — MUTLAK SERBEST
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Auth açık
        .requestMatchers(
          "/api/auth/login",
          "/api/auth/register"
        ).permitAll()

        // Diğer tüm API'ler JWT ister
        .requestMatchers("/api/**").authenticated()

        // Frontend vs
        .anyRequest().permitAll()
      )

      // JWT filter
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
