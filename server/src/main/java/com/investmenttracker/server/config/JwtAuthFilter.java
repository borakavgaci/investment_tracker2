package com.investmenttracker.server.config;

import com.investmenttracker.server.auth.JwtService;
import com.investmenttracker.server.user.User;
import com.investmenttracker.server.user.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String auth = request.getHeader("Authorization");
    if (auth == null || !auth.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = auth.substring("Bearer ".length()).trim();

    try {
      UUID userId = jwtService.getUserId(token);

      // user DB’de halen var mı kontrol (silindiyse token geçersiz say)
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        filterChain.doFilter(request, response);
        return;
      }

      User user = userOpt.get();
      List<SimpleGrantedAuthority> roles =
          user.isAdmin()
              ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
              : List.of(new SimpleGrantedAuthority("ROLE_USER"));

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(user, null, roles);

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);

    } catch (JwtException | IllegalArgumentException e) {
      // Token bozuk/expired → 401
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"code\":\"INVALID_TOKEN\",\"message\":\"Token is invalid or expired.\"}");
    }
  }
}
