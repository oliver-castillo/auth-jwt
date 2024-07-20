package com.app.auth.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthRequest {
  private final String username;
  private final String password;
}
