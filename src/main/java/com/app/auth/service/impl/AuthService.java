package com.app.auth.service.impl;

import com.app.auth.model.dto.request.AuthRequest;
import com.app.auth.model.dto.request.UserRequest;
import com.app.auth.model.dto.response.AuthResponse;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.model.mapper.IUserMapper;
import com.app.auth.repository.IUserRepository;
import com.app.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
  private final IUserRepository userRepository;
  private final IUserMapper userMapper;

  @Override
  public AuthResponse authenticate(AuthRequest authRequest) {
    return null;
  }

  @Override
  public OperationResponse register(UserRequest userRequest) {
    try {
      userRepository.save(userMapper.toEntity(userRequest));
      return new OperationResponse("User registered successfully");
    } catch (Exception e) {
      System.out.println("aquí" + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
