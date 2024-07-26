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
public class UserRequest {
  public interface Create {
  }

  public interface Update {
  }

  @NotNull(groups = {Update.class},
          message = "Enter a valid id")
  private final Long id;

  @NotBlank(groups = {
          Create.class,
          Update.class}, message = "Enter a name")
  @Pattern(groups = {
          Create.class,
          Update.class}, regexp = "^[a-zA-ZáéíóúñçÁÉÍÓÚÇÑ]+$", message = "Enter a valid name")
  @Length(groups = {
          Create.class,
          Update.class}, min = 3, max = 50, message = "Name must be between 3 and 50 characters")
  private final String name;

  @NotBlank(groups = {
          Create.class,
          Update.class}, message = "Enter a last name")
  @Pattern(groups = {
          Create.class,
          Update.class},
          regexp = "^[a-zA-ZáéíóúñçÁÉÍÓÚÇÑ]+$", message = "Enter a valid last name")
  @Length(groups = {
          Create.class,
          Update.class}, min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
  private final String lastName;

  @NotNull(groups = {
          Create.class,
          Update.class}, message = "Select a role")
  private final Role role;

  @NotBlank(groups = {
          Create.class,
          Update.class}, message = "Enter a username")
  @Pattern(groups = {
          Create.class,
          Update.class}, regexp = "^[a-zA-Z0-9\\-_.]+$", message = "Enter a valid username")
  @Length(groups = {
          Create.class,
          Update.class}, min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private final String username;

  @NotBlank(groups = {
          Create.class,
          Update.class}, message = "Enter a password")
  @Pattern(groups = {
          Create.class,
          Update.class}, regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$", message = "Enter a valid password")
  @Length(groups = {
          Create.class,
          Update.class}, min = 8, max = 100, message = "Password must have at least 8 characters")
  private String password;

  /*
   * Password:
   * Must contain at least 8 characters
   * At least one uppercase letter
   * At least one lowercase letter
   * At least one number
   * At least one special character (!@#$%^&*)
   */
}