package com.investmenttracker.server.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class AuthUtil {

    private AuthUtil() {}

    public static UUID requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
        }

        // Bizim Jwt filter genelde principal'ı UUID veya String olarak koyar.
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        if (principal instanceof String s) {
            try {
                return UUID.fromString(s);
            } catch (Exception e) {
                throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
            }
        }

        // Eğer başka tür principal basıldıysa (UserDetails vs.)
        // burada genişletilebilir.
        throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
    }
}
