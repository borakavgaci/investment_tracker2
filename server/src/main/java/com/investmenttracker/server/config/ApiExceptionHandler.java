package com.investmenttracker.server.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.HttpRequestMethodNotSupportedException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
        "timestamp", Instant.now().toString(),
        "code", "BAD_REQUEST",
        "message", ex.getMessage()
    ));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleUpstream(IllegalStateException ex) {
    // Finnhub gibi dış servis hatası: 502 uygun
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
        "timestamp", Instant.now().toString(),
        "code", "UPSTREAM_ERROR",
        "message", ex.getMessage()
    ));
  }
  @ExceptionHandler(Exception.class)
public ResponseEntity<?> handleAny(Exception ex) {
  ex.printStackTrace(); // terminalde tam stack görürsün
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
      "timestamp", Instant.now().toString(),
      "code", "INTERNAL_ERROR",
      "message", ex.getClass().getSimpleName() + ": " + ex.getMessage()
  ));
}
@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
  return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(
      "timestamp", Instant.now().toString(),
      "code", "METHOD_NOT_ALLOWED",
      "message", ex.getMessage()
  ));
}


}
