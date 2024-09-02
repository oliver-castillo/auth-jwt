package com.app.auth.model.dto.request;

import com.app.auth.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * DTO for {@link com.app.auth.model.entity.User}
 */
@AllArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {
    @NotNull(message = "Enter a valid id")
    private final Long id;

    @NotBlank(message = "Enter a name")
    @Pattern(regexp = "^[a-zA-ZáéíóúñçÁÉÍÓÚÇÑ]+$", message = "Enter a valid name")
    @Length(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private final String name;

    @NotBlank(message = "Enter a last name")
    @Pattern(
            regexp = "^[a-zA-ZáéíóúñçÁÉÍÓÚÇÑ]+$", message = "Enter a valid last name")
    @Length(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private final String lastName;

    @NotNull(message = "Select a role")
    private final Role role;

    @NotBlank(message = "Enter a username")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_.]+$", message = "Enter a valid username")
    @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private final String username;

    @NotBlank(message = "Enter a password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$", message = "Enter a valid password")
    @Length(min = 8, max = 100, message = "Password must have at least 8 characters")
    private String password;
}
