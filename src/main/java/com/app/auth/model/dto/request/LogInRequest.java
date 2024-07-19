package com.app.auth.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LogInRequest {
  private final String username;
  private final String password;
}
