package com.app.auth.service;

import com.app.auth.model.dto.request.UpdateUserRequest;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.model.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
  List<UserResponse> getAllUsers();

  UserResponse getUserById(Long id);

  OperationResponse updateUser(UpdateUserRequest request);

  OperationResponse deleteUser(Long id);
}
