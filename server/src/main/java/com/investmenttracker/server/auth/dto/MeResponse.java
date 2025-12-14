package com.investmenttracker.server.auth.dto;

import com.investmenttracker.server.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class MeResponse {
  public UUID id;
  public String name;
  public String surname;
  public String email;
  public boolean isAdmin;
  public LocalDateTime createdAt;

  public static MeResponse of(User u) {
    MeResponse r = new MeResponse();
    r.id = u.getId();
    r.name = u.getName();
    r.surname = u.getSurname();
    r.email = u.getEmail();
    r.isAdmin = u.isAdmin();
    r.createdAt = u.getCreatedAt();
    return r;
  }
}
