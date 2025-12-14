package com.investmenttracker.server.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class AuthUser {

    private AuthUser() {}

    public static UUID requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UUID u) return u;

        if (principal instanceof String s) {
            try { return UUID.fromString(s); }
            catch (Exception ignored) {}
        }

        // Bazı configlerde principal email olabilir; o durumda filter'ı userId set edecek şekilde güncellemek gerekir.
        throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
    }
}
