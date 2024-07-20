package com.app.auth.controller;

import com.app.auth.model.dto.request.UserRequest;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final IAuthService authService;

  @PostMapping("/register")
  public ResponseEntity<OperationResponse> register(@RequestBody UserRequest userRequest) {
    return new ResponseEntity<>(authService.register(userRequest), HttpStatus.CREATED);
  }
}
