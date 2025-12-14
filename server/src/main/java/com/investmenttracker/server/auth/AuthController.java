
package com.investmenttracker.server.auth;

import com.investmenttracker.server.auth.dto.RegisterRequest;
import com.investmenttracker.server.auth.dto.UserResponse;
import com.investmenttracker.server.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import com.investmenttracker.server.auth.dto.LoginRequest;
import com.investmenttracker.server.auth.dto.LoginResponse;
import com.investmenttracker.server.auth.dto.MeResponse;
import com.investmenttracker.server.user.User;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        User created = authService.register(req);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.getId()))
                .body(UserResponse.of(created));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(MeResponse.of(user));
    }
}
