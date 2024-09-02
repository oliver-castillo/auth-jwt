package com.app.auth.controller;

import com.app.auth.model.dto.request.UpdateUserRequest;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.model.dto.response.UserResponse;
import com.app.auth.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<OperationResponse> updateUser(@RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<OperationResponse> deleteUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
