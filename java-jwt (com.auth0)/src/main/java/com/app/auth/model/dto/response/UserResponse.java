package com.app.auth.model.dto.response;

import com.app.auth.model.enums.Role;

public record UserResponse(Long id, String name, String lastName, Role role, String username) {
}