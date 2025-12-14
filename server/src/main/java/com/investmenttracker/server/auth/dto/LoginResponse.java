package com.investmenttracker.server.auth.dto;

public class LoginResponse {
  public String token;
  public String tokenType = "Bearer";

  public LoginResponse(String token) {
    this.token = token;
  }
}
