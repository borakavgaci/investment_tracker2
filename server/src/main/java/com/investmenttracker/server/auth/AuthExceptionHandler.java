package com.investmenttracker.server.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        if ("EMAIL_ALREADY_EXISTS".equals(ex.getMessage())) {
            return ResponseEntity.status(409).body(Map.of(
                    "code", "EMAIL_ALREADY_EXISTS",
                    "message", "This email is already registered."));
        }
        if ("INVALID_CREDENTIALS".equals(ex.getMessage())) {
            return ResponseEntity.status(401).body(Map.of(
                    "code", "INVALID_CREDENTIALS",
                    "message", "Email or password is incorrect."));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "code", "BAD_REQUEST",
                "message", ex.getMessage()));
    }
}
