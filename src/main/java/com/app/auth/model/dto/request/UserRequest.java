package com.app.auth.model.dto.request;

import com.app.auth.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link com.app.auth.model.entity.User}
 */
@AllArgsConstructor
@Getter
public class UserRequest implements Serializable {
  private final String name;
  private final String lastName;
  private final Role role;
  private final String username;
  private final String password;
}