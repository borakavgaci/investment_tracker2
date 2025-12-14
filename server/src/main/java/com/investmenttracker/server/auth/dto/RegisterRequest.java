package com.investmenttracker.server.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

  @NotBlank @Size(max = 100)
  public String name;

  @NotBlank @Size(max = 100)
  public String surname;

  @NotBlank @Email @Size(max = 255)
  public String email;

  @NotBlank @Size(min = 8, max = 72)
  public String password;
}
