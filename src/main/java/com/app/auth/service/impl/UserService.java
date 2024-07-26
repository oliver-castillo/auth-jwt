package com.app.auth.service.impl;

import com.app.auth.model.dto.request.UserRequest;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.model.dto.response.UserResponse;
import com.app.auth.model.entity.User;
import com.app.auth.model.mapper.IUserMapper;
import com.app.auth.repository.IUserRepository;
import com.app.auth.service.IUserService;
import com.app.auth.util.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
  private final IUserRepository userRepository;
  private final IUserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
  }

  @Override
  public UserResponse getUserById(Long id) {
    return userRepository.findById(id).map(userMapper::toUserResponse).orElseThrow(
            () -> new NotFoundException("User not found"));
  }

  @Override
  public OperationResponse updateUser(UserRequest userRequest) {
    userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    User user = userRepository.findById(userRequest.getId()).orElseThrow(() -> new NotFoundException("User not found"));
    userRepository.save(userMapper.partialUpdate(userRequest, user));
    return new OperationResponse("User updated successfully");
  }

  @Override
  public OperationResponse deleteUser(Long id) {
    if (userRepository.findById(id).isPresent()) {
      userRepository.deleteById(id);
    }
    return new OperationResponse("User deleted successfully");
  }
}
