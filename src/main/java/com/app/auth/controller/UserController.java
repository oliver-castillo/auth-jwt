package com.app.auth.controller;

import com.app.auth.model.dto.request.UserRequest;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.model.dto.response.UserResponse;
import com.app.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
  private final IUserService userService;

  @GetMapping("/get-all")
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PatchMapping("/update")
  public ResponseEntity<OperationResponse> updateUser(@RequestBody @Validated(UserRequest.Update.class) UserRequest userRequest) {
    return ResponseEntity.ok(userService.updateUser(userRequest));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<OperationResponse> deleteUser(@PathVariable("id") Long id) {
    return ResponseEntity.ok(userService.deleteUser(id));
  }
}
