package com.investmenttracker.server.auth.dto;

import com.investmenttracker.server.user.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponse {
  public UUID id;
  public String name;
  public String surname;
  public String email;
  public LocalDateTime createdAt;

  public static UserResponse of(User u) {
    UserResponse r = new UserResponse();
    r.id = u.getId();
    r.name = u.getName();
    r.surname = u.getSurname();
    r.email = u.getEmail();
    r.createdAt = u.getCreatedAt();
    return r;
  }
}
