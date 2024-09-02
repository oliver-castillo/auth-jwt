package com.app.auth.service.impl.auth;

import com.app.auth.model.dto.request.AuthRequest;
import com.app.auth.model.dto.request.CreateUserRequest;
import com.app.auth.model.dto.response.AuthResponse;
import com.app.auth.model.dto.response.OperationResponse;
import com.app.auth.model.entity.User;
import com.app.auth.model.mapper.IUserMapper;
import com.app.auth.repository.IUserRepository;
import com.app.auth.service.IAuthService;
import com.app.auth.service.impl.jwt.JwtService;
import com.app.auth.util.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse authenticateUser(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return new AuthResponse(jwtService.generateToken(userDetails.getUsername()));
    }

    @Override
    public OperationResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new OperationResponse("User registered successfully");
        } else {
            throw new BadRequestException("Username already exists");
        }
    }
}
