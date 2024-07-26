package com.app.auth.controller;

import com.app.auth.model.dto.request.AuthRequest;
import com.app.auth.model.dto.request.UserRequest;
import com.app.auth.model.dto.response.AuthResponse;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final IAuthService authService;

  @PostMapping("/sign-up")
  public ResponseEntity<OperationResponse> signUp(@RequestBody @Validated(UserRequest.Create.class) UserRequest userRequest) {
    return new ResponseEntity<>(authService.createUser(userRequest), HttpStatus.CREATED);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<AuthResponse> signIn(@RequestBody @Valid AuthRequest authRequest) {
    return new ResponseEntity<>(authService.authenticateUser(authRequest), HttpStatus.OK);
  }
}
