package com.app.auth.service;

import com.app.auth.model.dto.request.AuthRequest;
import com.app.auth.model.dto.request.UserRequest;
import com.app.auth.model.dto.response.AuthResponse;
import com.app.auth.model.dto.response.OperationResponse;

public interface IAuthService {
  AuthResponse authenticate(AuthRequest authRequest);

  OperationResponse register(UserRequest userRequest);
}
