package com.shopflow.service;

import com.shopflow.dto.AuthResponse;
import com.shopflow.dto.LoginRequest;
import com.shopflow.dto.RegisterRequest;
import com.shopflow.entity.Role;
import com.shopflow.entity.User;
import com.shopflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User userEntity = new User();
        userEntity.setEmail(registerRequest.email());
        userEntity.setPassword(passwordEncoder.encode(registerRequest.password()));
        userEntity.setRole(Role.CUSTOMER);
        userEntity = userRepository.save(userEntity);
        return new AuthResponse(jwtService.generateToken(userEntity.getEmail()));
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}
