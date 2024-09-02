package com.app.auth.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public enum Role {
  ADMIN(Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE, Permission.DELETE)),
  USER(Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE));

  private final Set<Permission> permissions;
}
