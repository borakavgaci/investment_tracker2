package com.investmenttracker.server.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import java.util.UUID;

public final class AuthUtil {

    private AuthUtil() {}

    public static UUID requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        // Anonymous authentication varsa (Spring default)
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        if (!auth.isAuthenticated()) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
        }

        // JwtAuthFilter'da principal olarak UUID set ediyorsak
        if (principal instanceof UUID uuid) {
            return uuid;
        }

        // JwtAuthFilter'da principal olarak String (UUID string) set ediyorsak
        if (principal instanceof String s) {
            try {
                return UUID.fromString(s);
            } catch (Exception e) {
                throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
            }
        }

        /*
         * Eğer ileride UserDetails basarsan:
         *   principal instanceof UserDetails
         *   -> username = UUID string
         * burada genişletilir.
         */
        throw new IllegalStateException("UNAUTHORIZED_PRINCIPAL");
    }
}
